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

import org.glassfish.jersey.client.proxy.WebResourceFactory
import org.web3j.openapi.core.Web3jOpenApi
import java.lang.reflect.Proxy

object ClientFactory {

    /**
     * Builds a JAX-RS client with the given type [T] and service.
     */
    @JvmStatic
    @JvmOverloads
    fun <T : Web3jOpenApi> create(type: Class<T>, service: ClientService, token: String? = null): T {
        require(type.isInterface) { "Client class must be an interface" }

        val target = service.client.target(service.uri)
        token?.run { target.register(AuthenticationFilter.token(token)) }

        val client = WebResourceFactory.newResource(type, target)
        val handler = ClientInvocationHandler(target, client)

        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(type.classLoader, arrayOf(type), handler) as T
    }
}
