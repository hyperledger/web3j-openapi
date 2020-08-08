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

import io.epirus.web3j.Epirus
import io.epirus.web3j.gas.EpirusGasProvider
import io.epirus.web3j.gas.GasPrice
import mu.KLogging
import org.glassfish.hk2.api.Factory
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.openapi.server.Properties.GAS_PRICE
import org.web3j.openapi.server.Properties.NETWORK
import org.web3j.openapi.server.Properties.NODE_ADDRESS
import org.web3j.openapi.server.Properties.PRIVATE_KEY
import org.web3j.openapi.server.Properties.WALLET_FILE
import org.web3j.openapi.server.Properties.WALLET_PASSWORD
import org.web3j.protocol.Network
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
        val nodeAddress = configuration.getProperty(NODE_ADDRESS)?.toString()
        val network = configuration.getProperty(NETWORK)?.toString()
        return if (network != null && network.isNotEmpty())
            Epirus.buildWeb3j(Network.valueOf(network.toUpperCase()))
        else
            Web3j.build(HttpService(nodeAddress))
    }

    override fun dispose(web3j: Web3j) = web3j.shutdown()
}

class CredentialsFactory(
    @Context private val configuration: Configuration
) : Factory<Credentials>, KLogging() {

    override fun provide(): Credentials {
        val privateKey = configuration.getProperty(PRIVATE_KEY)?.toString()
        val walletFilePath = configuration.getProperty(WALLET_FILE)?.toString()
        val network = configuration.getProperty(NETWORK)?.toString()
        return if (!walletFilePath.isNullOrBlank()) {
            logger.info("Loading credentials from wallet file $walletFilePath")
            val walletFile = File(walletFilePath)
            val walletPassword = configuration.getProperty(WALLET_PASSWORD).toString()
            WalletUtils.loadCredentials(walletPassword, walletFile)
        } else if (!privateKey.isNullOrBlank()) {
            logger.info("Loading credentials from raw private key")
            Credentials.create(privateKey)
        } else if (network != null && network.isNotEmpty()) {
            val walletPath = System.getenv("EPIRUS_WALLET_PATH")
            val walletPassword = System.getenv().getOrDefault("EPIRUS_WALLET_PASSWORD", "")
            WalletUtils.loadCredentials(walletPassword, File(walletPath))
        } else {
            logger.warn("Missing credentials! Aborting.")
            throw IllegalStateException("Credentials missing!")
        }
    }

    override fun dispose(credentials: Credentials) {
    }
}

class ContractGasProviderFactory(
    @Context private val configuration: Configuration
) : Factory<ContractGasProvider> {

    override fun provide(): ContractGasProvider {
        val network = configuration.getProperty(NETWORK)?.toString()
        val gasPrice = configuration.getProperty(GAS_PRICE) as GasPrice? ?: GasPrice.High
        return if (network != null && network.isNotEmpty())
            EpirusGasProvider(Network.valueOf(network.toUpperCase()), gasPrice)
        else
            DefaultGasProvider()
    }

    override fun dispose(gasProvider: ContractGasProvider) {
    }
}
