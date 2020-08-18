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

import picocli.CommandLine
import java.util.Arrays
import java.util.Locale
import java.util.Objects
import java.util.stream.Stream

internal class EnvironmentVariableDefaultProvider(
    private val environment: Map<String, String>
) : CommandLine.IDefaultValueProvider {

    private val EPIRUS_VAR_PREFIX = "EPIRUS_"
    private val WEB3J_VAR_PREFIX = "WEB3J_"

    override fun defaultValue(argSpec: CommandLine.Model.ArgSpec): String? {
        return if (!argSpec.isOption) {
            null
        } else envVarNames(argSpec as CommandLine.Model.OptionSpec)
            .map { key: String? ->
                environment[key]
            }
            .filter { obj: String? -> Objects.nonNull(obj) }
            .findFirst()
            .orElse(null)
    }

    private fun envVarNames(spec: CommandLine.Model.OptionSpec): Stream<String?> {
        return Arrays.stream(spec.names())
            .filter { name: String -> name.startsWith("--") } // Only long options are allowed
            .flatMap { name: String ->
                Stream.of<String>(
                    EPIRUS_VAR_PREFIX,
                    WEB3J_VAR_PREFIX
                )
                    .map { prefix: String ->
                        prefix + nameToEnvVarSuffix(
                            name
                        )
                    }
            }
    }

    private fun nameToEnvVarSuffix(name: String): String {
        return name.substring("--".length).replace('-', '_').toUpperCase(Locale.US)
    }
}
