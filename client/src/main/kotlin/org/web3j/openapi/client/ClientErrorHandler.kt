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
import java.lang.reflect.Proxy
import java.util.Arrays
import javax.ws.rs.ClientErrorException

/**
 * Invocation handler for proxied resources. Implements an exception mapping mechanism to avoid reporting
 * [ClientErrorException]s to the client.
 */
internal class ClientErrorHandler<T>(
    private val client: T,
    private val mapper: (ClientErrorException) -> RuntimeException
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        logger.debug { "Invoking method $method with arguments ${Arrays.toString(args)}" }

        try {
            // Invoke the original method on the client
            return method.invoke(client, *(args ?: arrayOf())).let {
                if (Proxy.isProxyClass(it.javaClass)) {
                    // The result is a Jersey web resource
                    // so we need to wrap it again
                    Proxy.newProxyInstance(
                        method.returnType.classLoader,
                        arrayOf(method.returnType),
                        ClientErrorHandler(it, mapper)
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
        return mapper.invoke(error)
    }

    companion object : KLogging()
}
