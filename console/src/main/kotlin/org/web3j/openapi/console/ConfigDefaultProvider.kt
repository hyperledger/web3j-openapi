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
package org.web3j.openapi.console

import picocli.CommandLine.IDefaultValueProvider
import picocli.CommandLine.Model.ArgSpec
import picocli.CommandLine.Model.OptionSpec
import java.io.File
import java.io.FileReader
import java.util.Properties

internal class ConfigDefaultProvider(
    private val configFile: File?,
    private val environment: Map<String, String>,
    private val defaultFile: File
) : IDefaultValueProvider {

    override fun defaultValue(argSpec: ArgSpec): String? {
        return configFile?.run {
            getPropertyFromFile(configFile, argSpec)
        } ?: getPropertyFromFile(defaultFile, argSpec)
        ?: environment[getEnvironmentName(argSpec)]
    }

    private fun getPropertyName(argSpec: ArgSpec): String {
        return (argSpec as OptionSpec)
            .longestName()
            .removePrefix("--")
            .replace("-", ".")
            .prependIndent("web3j.openapi.")
    }

    private fun getEnvironmentName(argSpec: ArgSpec): String {
        return (argSpec as OptionSpec)
            .longestName()
            .toUpperCase()
            .removePrefix("--")
            .replace("-", "_")
            .prependIndent("WEB3J_OPENAPI_")
    }

    private fun getPropertyFromFile(configFile: File, argSpec: ArgSpec): String? {
        return if (configFile.exists()) {
            FileReader(configFile).use {
                Properties().run {
                    load(it)
                    getProperty(getPropertyName(argSpec))
                }
            }
        } else {
            null
        }
    }
}
