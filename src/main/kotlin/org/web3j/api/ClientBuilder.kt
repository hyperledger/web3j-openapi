package org.web3j.api

import org.glassfish.jersey.client.proxy.WebResourceFactory
import java.lang.reflect.Proxy
import javax.ws.rs.ClientErrorException

object ClientBuilder : KLogging() {

    /**
     * Builds a JAX-RS client with the given type [T].
     */
    fun <T> build(type: Class<T>, service: ContractService, token: String? = null): T {
        require(type.isInterface) { "Client class must be an interface" }

        val target = service.client.target(service.uri)
        token?.run { target.register(AuthenticationFilter.token(token)) }

        return WebResourceFactory.newResource(type, target)
    }

    /**
     * Builds a JAX-RS client which maps client errors to other exceptions.
     */
    fun <T> build(
        type: Class<T>,
        service: ContractService,
        mapper: (ClientErrorException) -> RuntimeException,
        token: String? = null
    ): T {
        val client = build(type, service, token)
        val handler = ClientErrorHandler(client, mapper)

        @Suppress("UNCHECKED_CAST")
        return Proxy.newProxyInstance(type.classLoader, arrayOf(type), handler) as T
    }
}