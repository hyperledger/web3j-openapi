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
package org.web3j.openapi.server.console.defaultproviders

import org.web3j.openapi.server.console.ConsoleConfiguration
import picocli.CommandLine

internal class ConfigMapper(
    private val consoleConfiguration: ConsoleConfiguration?
) {

    fun value(param: CommandLine.Model.ArgSpec): String? {
        val stringParam = param.toPropertyName()
        return when {
            stringParam.contains("config.file") -> consoleConfiguration?.configFileOptions?.configFile?.absolutePath
            stringParam.contains("private.key") -> consoleConfiguration?.credentialsOptions?.privateKey
            stringParam.contains("wallet.path") -> consoleConfiguration?.credentialsOptions?.walletOptions?.walletFile?.absolutePath
            stringParam.contains("wallet.password") -> consoleConfiguration?.credentialsOptions?.walletOptions?.walletPassword
            stringParam.contains("endpoint") -> consoleConfiguration?.networkOptions?.endpoint?.toString()
            stringParam.contains("projectName") -> consoleConfiguration?.projectOptions?.projectName
            stringParam.contains("contextPath") -> consoleConfiguration?.projectOptions?.contextPath
            stringParam.contains("host") -> consoleConfiguration?.serverOptions?.host?.hostAddress
            stringParam.contains("port") -> consoleConfiguration?.serverOptions?.port?.toString()
            stringParam.contains("contract.addresses") -> consoleConfiguration?.contractAddresses?.map { entry -> "${entry.key}=${entry.value}" }?.joinToString(",")
            else -> null // If you add other fields to here, make sure to add them to the Env variables too
        }
    }

    /**
     * Property names in configuration files are written in a specific way.
     * For example: <code>--wallet-password</code> becomes <code>wallet.password</code>
     * depending on the config file type.
     * for non composed names: <code>--port</code> becomes <code>port</code>
     */
    private fun CommandLine.Model.ArgSpec.toPropertyName(): String {
        return (this as CommandLine.Model.OptionSpec)
            .longestName()
            .removePrefix("--")
            .replace("-", ".")
    }
}
