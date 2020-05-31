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

data class OpenApiServerConfig(
    val projectName: String,
    val nodeEndpoint: String,
    val privateKey: String,
    val walletFilePath: String,
    val walletPassword: String,
    val host: String,
    val port: Int
) {
    init {
        if (privateKey.isBlank() && walletFilePath.isBlank()) {
            throw IllegalArgumentException("Credentials not found!")
        } else if (!walletFilePath.isBlank() && walletPassword.isBlank()) {
            throw IllegalArgumentException("Wallet file $walletFilePath password not found!")
        }
    }
}
