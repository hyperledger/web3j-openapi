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
package org.web3j.openapi.server.config

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.glassfish.jersey.logging.LoggingFeature
import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.server.ServerProperties
import org.slf4j.bridge.SLF4JBridgeHandler
import org.web3j.crypto.Credentials
import org.web3j.openapi.server.TransactionExceptionMapper
import org.web3j.openapi.server.UnsupportedOperationExceptionMapper
import org.web3j.openapi.server.JsonParseExceptionMapper
import org.web3j.openapi.server.JsonMappingExceptionMapper
import org.web3j.openapi.server.ContractCallExceptionMapper
import org.web3j.openapi.server.IllegalArgumentExceptionMapper
import org.web3j.openapi.server.Properties
import org.web3j.openapi.server.ContractGasProviderFactory
import org.web3j.openapi.server.CredentialsFactory
import org.web3j.openapi.server.Web3jFactory
import org.web3j.openapi.server.spi.OpenApiResourceProvider
import org.web3j.protocol.Web3j
import org.web3j.tx.gas.ContractGasProvider
import java.util.ServiceLoader
import java.util.logging.Logger
import javax.inject.Singleton

/**
 * The JAX-RS application configuration.
 *
 * @see OpenApiResource
 * @see OpenApiResourceProvider
 */
class OpenApiResourceConfig(
    serverConfig: OpenApiServerConfig
) : ResourceConfig() {

//    /**
//     * Used mainly for testing.
//     *
//     * The given Web3j, transaction manager and gas provider instances
//     * will override the server configuration.
//     */
//    constructor(
//        web3j: Web3j,
//        gasProvider: ContractGasProvider,
//        transactionManager: TransactionManager,
//        serverConfig: OpenApiServerConfig
//    ) : this(serverConfig) {
//        property(Properties.WEB3J, web3j)
//        property(Properties.GAS_PROVIDER, gasProvider)
//        property(Properties.TRANSACTION_MANAGER, transactionManager)
//    }

    private val mapper = jacksonObjectMapper()
        .setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        .enable(SerializationFeature.INDENT_OUTPUT)

    init {
        // Register all Web3j OpenAPI resources in the classpath
        ServiceLoader.load(OpenApiResourceProvider::class.java).forEach {
            register(it.get())
        }

        register(OpenApiResource::class.java)
        register(JsonMappingExceptionMapper::class.java)
        register(IllegalArgumentExceptionMapper::class.java)
        register(ContractCallExceptionMapper::class.java)
        register(JsonParseExceptionMapper::class.java)
        register(TransactionExceptionMapper::class.java)
        register(UnsupportedOperationExceptionMapper::class.java)
        register(JacksonJaxbJsonProvider(mapper, arrayOf(Annotations.JACKSON)))
        register(LoggingFeature(logger))
        register(InjectionBinder())

        property(ServerProperties.APPLICATION_NAME, serverConfig.projectName)
        property(Properties.NODE_ADDRESS, serverConfig.nodeEndpoint)
        property(Properties.PRIVATE_KEY, serverConfig.privateKey)
        property(Properties.WALLET_FILE, serverConfig.walletFile?.absolutePath)
        property(Properties.WALLET_PASSWORD, serverConfig.walletPassword)
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

        private val logger = Logger.getLogger(OpenApiResourceConfig::class.java.canonicalName)!!
    }
}
