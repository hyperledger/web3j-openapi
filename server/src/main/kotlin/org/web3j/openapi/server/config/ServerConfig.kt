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

import org.aeonbits.owner.ConfigFactory
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import java.io.File

class ServerConfig {

    private val configOptions: ConfigOptions = ConfigFactory.create(ConfigOptions::class.java)

    fun getHost(): String {
        return configOptions.host()
    }

    fun getPort(): Int {
        return configOptions.port()
    }

    fun getNodeEndpoint(): String {
        return configOptions.nodeEndpoint()
    }

    fun getProjectName(): String {
        return configOptions.projectName()
    }

    fun getCredentials(): Credentials {
        return if (!configOptions.walletFile().isNullOrBlank()) {
            val walletFile = File(configOptions.walletFile()!!)
            val walletPassword = configOptions.walletPassword()
            WalletUtils.loadCredentials(walletPassword, walletFile)
        } else if (!configOptions.privateKey().isNullOrBlank())
            Credentials.create(configOptions.privateKey())
        else throw NoSuchFieldException("Credentials missing!")
    }
}
