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
import org.web3j.openapi.core.Web3jOpenApi
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy
import java.util.Arrays
import java.util.concurrent.CompletableFuture
import javax.ws.rs.ClientErrorException
import javax.ws.rs.Path
import javax.ws.rs.client.WebTarget
import javax.ws.rs.sse.SseEventSource
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.jvmErasure

/**
 * Invocation handler for proxied resources.
 *
 * Handles contract events using a Server-Sent Event (SSE) request.
 *
 * Also implements an exception mapping mechanism to avoid reporting
 * [ClientErrorException]s to the client.
 */
internal class ClientInvocationHandler<T>(
    private val apiClass: Class<out Web3jOpenApi>,
    private val target: WebTarget,
    private val client: T
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        logger.debug { "Invoking method $method with arguments ${Arrays.toString(args)}" }

        return if (method.isEvent()) {
            invokeOnEvent(method, args!!)
        } else {
            invokeClient(method, args)
        }
    }

    private fun invokeOnEvent(method: Method, args: Array<out Any>): CompletableFuture<Void> {
        logger.debug { "Invoking event method: $method" }

        @Suppress("UNCHECKED_CAST")
        val consumer = args[0] as (Any) -> Unit
        val result = CompletableFuture<Void>()
        val path = methodPath(method, apiClass, "0x42699a7612a82f1d9c36148af9c77354759b210b")

        SseEventSource.target(target.path(path)).build().apply {
            register(
                { consumer.invoke(it.readData(args[0].typeArguments[0])) },
                { result.completeExceptionally(it) },
                { result.complete(null) }
            )
            open()
        }

        return result
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
                        ClientInvocationHandler(apiClass, target, it)
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

    private fun methodPath(method: Method, api: Class<out Web3jOpenApi>, contractAddress: String): String {

        val contractResource: KProperty<*> = api.kotlin.members
            .filterIsInstance<KProperty<*>>()
            .first { it.name == "contracts" }
            .run {
                returnType.jvmErasure.members
                    .filterIsInstance<KProperty<*>>()
                    .first { lifecycle ->
                        method.declaringClass == lifecycle.returnType.jvmErasure.functions.first { function ->
                            function.name == "load"
                        }.returnType.jvmErasure.java
                    }
            }

        val apiPath = api.kotlin.findAnnotation<Path>()!!
        val contractPath = contractResource.getter.findAnnotation<Path>()!!
        val eventName = method.name.removePrefix("on")

        return "${apiPath.value}/contracts/${contractPath.value}/$contractAddress/${eventName}"
    }

    private fun Method.isEvent() = parameterTypes.size == 1
            && parameterTypes[0] == Function1::class.java
            && returnType == CompletableFuture::class.java

    private val Any.typeArguments: List<Class<*>>
        get() {
            val parameterizedType = this::class.java.genericInterfaces[0] as ParameterizedType
            return parameterizedType.actualTypeArguments.map { it as Class<*> }
        }

    companion object : KLogging()
}
