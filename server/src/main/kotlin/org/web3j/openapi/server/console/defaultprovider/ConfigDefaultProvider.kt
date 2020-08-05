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

import picocli.CommandLine.IDefaultValueProvider
import picocli.CommandLine.Model.ArgSpec
import picocli.CommandLine.Model.OptionSpec
import java.io.File

internal class ConfigDefaultProvider(
    private val configFile: File?,
    private val environment: Map<String, String>,
    defaultFile: String
) : IDefaultValueProvider {

    private val consoleConfigMapper =
        ConfigMapper(configFile)
    private val defaultConsoleConfigMapper =
        ConfigMapper(
            when {
                File("$defaultFile.yaml").exists() -> File("$defaultFile.yaml")
                File("$defaultFile.json").exists() -> File("$defaultFile.json")
                else -> File("$defaultFile.properties")
            }
        )

    /**
     * Default value returns the default value of a certain field of
     * the ConsoleConfiguration class.
     *
     * This won't be called unless the parameters weren't passed directly
     * to the CLI.
     * If so, it behaves in the following order:
     *
     *      - checks in the configuration file passed to the CLI (if it exists)
     *      - next, checks the default configuration file which it in
     *          <code>~/.epirus/web3j.openapi.{extension}</code>
     *      - finally, checks the environment variables
     *
     * Take a look on <code>ConfigMapper.consoleConfiguration</code> for more information on
     * the supported file types, ie {extensions}.
     */
    override fun defaultValue(argSpec: ArgSpec): String? {
        return configFile?.run {
            getPropertyFromFile(argSpec)
        } ?: getPropertyFromFile(argSpec, true)
        ?: environment[getEnvironmentName(argSpec)]
    }

    /**
     * Property names in configuration files are written in a specific way.
     * For example: <code>--wallet-password</code> becomes <code>wallet.password</code>
     * for non composed names: <code>--port</code> becomes <code>port</code>
     */
    private fun getPropertyName(argSpec: ArgSpec): String {
        return (argSpec as OptionSpec)
            .longestName()
            .removePrefix("--")
            .replace("-", ".")
    }

    /**
     * Property names in environment variables are written in a specific way.
     * For example:
     * <code>--wallet-password</code> becomes <code>WEB3j_OPENAPI_WALLET_PASSWORD</code>
     * for non composed names: <code>--port</code> becomes <code>WEB3J_OPENAPI_PORT</code>
     */
    private fun getEnvironmentName(argSpec: ArgSpec): String {
        return (argSpec as OptionSpec)
            .longestName()
            .toUpperCase()
            .removePrefix("--")
            .replace("-", "_")
            .prependIndent("WEB3J_OPENAPI_")
    }

    private fun getPropertyFromFile(argSpec: ArgSpec, defaultFile: Boolean = false): String? {
        return if (defaultFile) defaultConsoleConfigMapper.value(getPropertyName(argSpec))
        else consoleConfigMapper.value(getPropertyName(argSpec))
    }
}
