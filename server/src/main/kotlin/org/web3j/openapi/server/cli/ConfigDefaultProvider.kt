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
package org.web3j.openapi.server.cli

import picocli.CommandLine.IDefaultValueProvider
import picocli.CommandLine.Model.OptionSpec
import picocli.CommandLine.Model.ArgSpec
import java.io.File
import java.io.FileReader
import java.util.Optional
import java.util.Properties

class ConfigDefaultProvider() : IDefaultValueProvider {

    constructor(configFile: Optional<File>, environment: Map<String, String>, defaultFile: File) : this() {
        this.configFile = configFile
        this.environment = environment
        this.defaultFile = defaultFile
    }

    lateinit var configFile: Optional<File>
    lateinit var environment: Map<String, String>
    lateinit var properties: Properties
    lateinit var defaultFile: File

    override fun defaultValue(argSpec: ArgSpec): String? {
        if (configFile.isPresent) {
            properties = Properties()
            FileReader(configFile.get()).use { reader -> properties.load(reader) }
            return properties.getProperty(getPropertyName(argSpec))
        }
        if (defaultFile.exists()) {
            properties = Properties()
            FileReader(defaultFile).use { reader -> properties.load(reader) }
            return properties.getProperty(getPropertyName(argSpec))
        }
        return environment[getEnvironmentName(argSpec)]
    }

    private fun getPropertyName(argSpec: ArgSpec) : String {
        return (argSpec as OptionSpec)
            .longestName()
            .removePrefix("--")
            .replace("-", ".")
            .prependIndent("web3j.openapi.")
    }

    private fun getEnvironmentName(argSpec: ArgSpec) : String {
        return (argSpec as OptionSpec)
            .longestName()
            .toUpperCase()
            .removePrefix("--")
            .replace("-", "_")
            .prependIndent("WEB3J_OPENAPI_")
    }
}
