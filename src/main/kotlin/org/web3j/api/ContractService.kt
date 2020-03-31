package org.web3j.api

import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.glassfish.jersey.logging.LoggingFeature
import java.util.logging.Level
import java.util.logging.Logger
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder

class ContractService(
    val uri: String,
    readTimeout: Int = DEFAULT_READ_TIMEOUT,
    connectTimeout: Int = DEFAULT_CONNECT_TIMEOUT
) : AutoCloseable {

    internal val client: Client by lazy {

        val config = ClientConfig().apply {
            // Redirect ALL logs to SLFJ using logging.properties
            register(LoggingFeature(logger.apply { level = Level.ALL }, Short.MAX_VALUE.toInt()))
            register(JacksonJaxbJsonProvider(mapper, arrayOf(Annotations.JACKSON)))
            property(ClientProperties.READ_TIMEOUT, readTimeout)
            property(ClientProperties.CONNECT_TIMEOUT, connectTimeout)
        }

        ClientBuilder.newClient(config)
    }

    override fun close() = client.close()

    companion object {
        const val DEFAULT_READ_TIMEOUT: Int = 30000
        const val DEFAULT_CONNECT_TIMEOUT: Int = 30000

//        init {
//            SLF4JBridgeHandler.removeHandlersForRootLogger()
//            SLF4JBridgeHandler.install()
//        }

        private val logger = Logger.getLogger(ContractService::class.java.canonicalName)!!
    }
}