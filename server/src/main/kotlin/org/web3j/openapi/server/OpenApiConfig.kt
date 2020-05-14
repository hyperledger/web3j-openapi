/*
 * Copyright 2020 Web3 Labs Ltd.
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
package org.web3j.openapi.server

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.aeonbits.owner.ConfigFactory
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.slf4j.bridge.SLF4JBridgeHandler
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.gas.ContractGasProvider
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Singleton

class OpenApiConfig() : ResourceConfig() {

    val serverConfig: ServerConfig = ConfigFactory.create(ServerConfig::class.java)

    private val mapper = jacksonObjectMapper()
        .setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        .enable(SerializationFeature.INDENT_OUTPUT)

    init {
        register(JsonMappingExceptionMapper::class.java)
        register(IllegalArgumentExceptionMapper::class.java)
        register(ContractCallExceptionMapper::class.java)
        register(JacksonJaxbJsonProvider(mapper, arrayOf(Annotations.JACKSON)))
        register(LoggingFeature(logger.apply { level = Level.ALL }, Short.MAX_VALUE.toInt())) // FIXME Why no logs?
        register(InjectionBinder())

        property(ServerProperties.APPLICATION_NAME, serverConfig.projectName())
        property(Properties.NODE_ADDRESS, serverConfig.nodeEndpoint())
        property(Properties.PRIVATE_KEY, serverConfig.privateKey())
        property(Properties.WALLET_FILE, serverConfig.walletFile())
        property(Properties.WALLET_PASSWORD, serverConfig.walletPassword())
    }

    private class InjectionBinder : AbstractBinder() {
        override fun configure() {
            bindFactory(Web3jFactory::class.java)
                .to(Web3j::class.java).`in`(Singleton::class.java)
            bindFactory(CredentialsFactory::class.java)
                .to(Credentials::class.java).`in`(Singleton::class.java)
            bindFactory(ContractGasProviderFactory::class.java)
                .to(ContractGasProvider::class.java).`in`(Singleton::class.java)
        }
    }

    companion object {
        init {
            SLF4JBridgeHandler.removeHandlersForRootLogger()
            SLF4JBridgeHandler.install()
        }

        private val logger = Logger.getLogger(OpenApiConfig::class.java.canonicalName)!!
    }
}
