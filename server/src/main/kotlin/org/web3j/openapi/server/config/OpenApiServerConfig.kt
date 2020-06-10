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

import java.io.File
import java.net.URL

data class OpenApiServerConfig(
    val projectName: String,
    val nodeEndpoint: URL,
    val privateKey: String? = null,
    val walletFile: File? = null,
    val walletPassword: String? = null,
    val host: String,
    val port: Int
) {
    init {
        if (privateKey == null && walletFile == null) {
            throw IllegalArgumentException("Invalid credentials, use a private key or wallet file")
        } else if (walletFile != null) {
            if (!walletFile.exists())
                throw IllegalArgumentException("Wallet file $walletFile not found!")
            else if (walletPassword == null)
                throw IllegalArgumentException("Wallet file $walletFile password not found!")
        }
    }
}
