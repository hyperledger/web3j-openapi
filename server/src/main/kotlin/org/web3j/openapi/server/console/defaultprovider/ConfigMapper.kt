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
package org.web3j.openapi.server.console.defaultprovider

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.web3j.openapi.server.console.ConsoleConfiguration
import java.io.File

class ConfigMapper(
    private val configFile: File?
) {
    private val consoleConfiguration: ConsoleConfiguration?
    get() {
        return when (configFile?.extension) {
            "yaml" -> ObjectMapper(YAMLFactory()).readValue(configFile, ConsoleConfiguration::class.java)
            "json" -> ObjectMapper().readValue(configFile, ConsoleConfiguration::class.java)
            else -> JavaPropsMapper().readValue(configFile, ConsoleConfiguration::class.java)
        }
    }

    fun value(type: String): String? {
        return when {
            type.contains("config.file") -> consoleConfiguration?.configFileOptions?.configFile?.absolutePath
            type.contains("private.key") -> consoleConfiguration?.credentialsOptions?.privateKey
            type.contains("wallet.file") -> consoleConfiguration?.credentialsOptions?.walletOptions?.walletFile?.absolutePath
            type.contains("wallet.password") -> consoleConfiguration?.credentialsOptions?.walletOptions?.walletPassword
            type.contains("endpoint") -> consoleConfiguration?.networkOptions?.endpoint?.toString()
            type.contains("projectName") -> consoleConfiguration?.projectOptions?.projectName
            type.contains("contextPath") -> consoleConfiguration?.projectOptions?.contextPath
            type.contains("host") -> consoleConfiguration?.serverOptions?.host?.hostAddress
            type.contains("port") -> consoleConfiguration?.serverOptions?.port?.toString()
            type.contains("contract.addresses") -> consoleConfiguration?.contractAddresses?.map { entry -> "${entry.key}=${entry.value}" }?.joinToString(",")
            else -> null
        }
    }
}
