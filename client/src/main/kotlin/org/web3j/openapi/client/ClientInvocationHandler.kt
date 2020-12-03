/*
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.openapi.client

import mu.KLogging
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import javax.ws.rs.ClientErrorException
import javax.ws.rs.client.WebTarget
import javax.ws.rs.sse.SseEventSource

/**
 * Invocation handler for proxied resources.
 *
 * Handles contract events using a Server-Sent Event (SSE) request.
 *
 * Also implements an exception mapping mechanism to avoid reporting
 * [ClientErrorException]s to the client.
 *
 * @see [org.web3j.openapi.core.EventResource.onEvent]
 * @see [ClientException]
 */
internal class ClientInvocationHandler(
    private val target: WebTarget,
    private val client: Any
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        return if (method.isEvent()) {
            logger.debug { "Invoking event method: $method" }
            @Suppress("UNCHECKED_CAST")
            invokeOnEvent(args!![0] as Consumer<Any>)
        } else {
            logger.debug { "Invoking client method: $method" }
            invokeClient(method, args)
        }
    }

    private fun <T> invokeOnEvent(onEvent: Consumer<T>): CompletableFuture<Void> {
        @Suppress("UNCHECKED_CAST")
        val eventType = onEvent.typeArguments[0] as Class<T>
        val source = SseEventSource.target(clientTarget()).build()
        return SseEventSourceResult(source, onEvent, eventType).also {
            it.open()
        }
    }

    private fun invokeClient(method: Method, args: Array<out Any>?): Any {
        try {
            // Invoke the original method on the client
            return method.invoke(client, *(args ?: arrayOf())).let {
                if (Proxy.isProxyClass(it.javaClass)) {
                    // The result is a Jersey web resource
                    // so we need to wrap it again
                    Proxy.newProxyInstance(
                        method.returnType.classLoader,
                        arrayOf(method.returnType),
                        ClientInvocationHandler(target, it)
                    )
                } else {
                    it
                }
            }
        } catch (e: InvocationTargetException) {
            throw handleInvocationException(e, method)
        } catch (e: ClientErrorException) {
            throw handleClientError(e, method)
        }
    }

    private fun handleInvocationException(error: InvocationTargetException, method: Method): Throwable {
        return error.targetException.let {
            if (it is ClientErrorException) {
                handleClientError(it, method)
            } else {
                logger.error {
                    "Unexpected exception while invoking method $method: " +
                            (error.message ?: error::class.java.canonicalName)
                }
                error.targetException
            }
        }
    }

    private fun handleClientError(error: ClientErrorException, method: Method): RuntimeException {
        logger.error {
            "Client exception while invoking method $method: " +
                    (error.message ?: error.response.statusInfo.reasonPhrase)
        }
        return ClientException.of(error)
    }

    private fun clientTarget(): WebTarget {
        val resourcePath = client.toString()
            .removePrefix("JerseyWebTarget { ")
            .removeSuffix(" }")
            .run { URL(this).path }
        return target.path(resourcePath)
    }

    private fun Method.isEvent() = name == "onEvent" &&
            parameterTypes.size == 1 &&
            parameterTypes[0] == Consumer::class.java &&
            returnType == CompletableFuture::class.java

    private val Any.typeArguments: List<Class<*>>
        get() {
            val parameterizedType = this::class.java.genericInterfaces[0] as ParameterizedType
            return parameterizedType.actualTypeArguments.map { it as Class<*> }
        }

    private class SseEventSourceResult<T>(
        private val source: SseEventSource,
        onEvent: Consumer<T>,
        eventType: Class<T>
    ) : CompletableFuture<Void>() {
        init {
            source.register(
                { onEvent.accept(it.readData(eventType)) },
                { completeExceptionally(it) },
                { complete(null) }
            )
            whenComplete { _, _ ->
                // Close the source gracefully by client
                if (source.isOpen) source.close()
            }
        }
        fun open() {
            Thread {
                source.open()
                while (source.isOpen) {
                    logger.debug { "Listening on event source..." }
                    Thread.sleep(5000)
                }
            }.start()
        }
    }

    companion object : KLogging()
}
