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

internal class EnvironmentVariableDefaultProvider(
    private val environment: Map<String, String>
) : CommandLine.IDefaultValueProvider {

    private val WEB3J_VAR_PREFIX = "WEB3J_"
    private val WEB3J_OPENAPI_VAR_PREFIX = "WEB3J_OPENAPI_"
    private val OPENAPI_SPECIFIC_VARIABLES = listOf("NAME", "CONTEXT_PATH", "HOST", "PORT", "CONTRACT_ADDRESSES", "CONFIG_FILE")

    override fun defaultValue(argSpec: CommandLine.Model.ArgSpec) = environment[argSpec.toEnvironmentName()]

    /**
     * Property names in environment variables are written in a specific way.
     * For example:
     * <code>--wallet-password</code> becomes <code>WALLET_PASSWORD</code>
     * for non composed names: <code>--port</code> becomes <code>PORT</code>
     *
     * and then, get prepended with the right prefix:
     *      WEB3J_OPENAPI_ : if it is an OpenAPI specific variable: "NAME", "CONTEXT_PATH", "HOST", "PORT"
     *      WEB3J_ : otherwise
     */
    private fun CommandLine.Model.ArgSpec.toEnvironmentName(): String {
        return (this as CommandLine.Model.OptionSpec)
            .longestName()
            .toUpperCase()
            .removePrefix("--")
            .replace("-", "_")
            .toPrependedEnvVarName()
    }

    private fun String.toPrependedEnvVarName(): String {
        return "${if (OPENAPI_SPECIFIC_VARIABLES.contains(this)) WEB3J_OPENAPI_VAR_PREFIX else WEB3J_VAR_PREFIX}$this"
    }
}
