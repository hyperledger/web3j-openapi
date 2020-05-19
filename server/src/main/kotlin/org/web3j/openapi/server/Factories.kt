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

import mu.KLogging
import org.glassfish.hk2.api.Factory
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.openapi.server.Properties.NODE_ADDRESS
import org.web3j.openapi.server.Properties.PRIVATE_KEY
import org.web3j.openapi.server.Properties.WALLET_FILE
import org.web3j.openapi.server.Properties.WALLET_PASSWORD
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import java.io.File
import javax.ws.rs.core.Configuration
import javax.ws.rs.core.Context

class Web3jFactory(
    @Context private val configuration: Configuration
) : Factory<Web3j> {

    override fun provide(): Web3j {
        val nodeAddress = configuration.getProperty(NODE_ADDRESS).toString()
        return Web3j.build(HttpService(nodeAddress))
    }

    override fun dispose(web3j: Web3j) {
        web3j.shutdown()
    }
}

class CredentialsFactory(
    @Context private val configuration: Configuration
) : Factory<Credentials>, KLogging() {

    override fun provide(): Credentials {
        val privateKey = configuration.getProperty(PRIVATE_KEY).toString()
        val walletFilePath = configuration.getProperty(WALLET_FILE).toString()
        return if (!walletFilePath.isBlank()) {
            logger.debug("Loading credentials from wallet file $walletFilePath")
            val walletFile = File(walletFilePath)
            val walletPassword = configuration.getProperty(WALLET_PASSWORD).toString()
            WalletUtils.loadCredentials(walletPassword, walletFile)
        } else if (!privateKey.isBlank()) {
            logger.debug("Loading credentials from raw private key")
            Credentials.create(privateKey)
        } else {
            logger.warn("Missing credentials! Aborting.")
            throw NoSuchFieldException("Credentials missing!")
        }
    }

    override fun dispose(credentials: Credentials) {
    }
}

class ContractGasProviderFactory(
    @Context private val configuration: Configuration
) : Factory<ContractGasProvider> {

    override fun provide(): ContractGasProvider {
        // TODO Use server properties to instantiate the provider
        return DefaultGasProvider()
    }

    override fun dispose(transactionManager: ContractGasProvider) {
    }
}
