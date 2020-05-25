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

import javassist.NotFoundException

class OpenApiServerConfigBuilder() {
    private lateinit var projectName: String
    private lateinit var nodeEndpoint: String
    private lateinit var privateKey: String
    private lateinit var walletFilePath: String
    private lateinit var walletPassword: String
    private lateinit var host: String
    private var port: Int = 0

    fun setProjectName(projectName: String): OpenApiServerConfigBuilder {
        this.projectName = projectName
        return this
    }
    fun setNodeEndpoint(nodeEndpoint: String): OpenApiServerConfigBuilder {
        this.nodeEndpoint = nodeEndpoint
        return this
    }
    fun setPrivateKey(privateKey: String): OpenApiServerConfigBuilder {
        this.privateKey = privateKey
        return this
    }
    fun setWalletFilePath(walletFile: String): OpenApiServerConfigBuilder {
        this.walletFilePath = walletFile
        return this
    }
    fun setWalletPassword(walletPassword: String): OpenApiServerConfigBuilder {
        this.walletPassword = walletPassword
        return this
    }
    fun setHost(host: String): OpenApiServerConfigBuilder {
        this.host = host
        return this
    }
    fun setPort(port: Int): OpenApiServerConfigBuilder {
        this.port = port
        return this
    }

    fun build(): OpenApiServerConfig {
        if (privateKey.isNullOrBlank() && walletFilePath.isNullOrBlank()) {
            throw NotFoundException("Credentials not found!")
        } else if (!walletFilePath.isNullOrBlank() && walletPassword.isNullOrBlank()) {
            throw NotFoundException("Wallet file $walletFilePath password not found !")
        }
        return OpenApiServerConfig(
            projectName,
            nodeEndpoint,
            privateKey,
            walletFilePath,
            walletPassword,
            host,
            port
        )
    }
}
