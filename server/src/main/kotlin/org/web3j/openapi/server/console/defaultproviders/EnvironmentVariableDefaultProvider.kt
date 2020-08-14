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

    override fun defaultValue(argSpec: CommandLine.Model.ArgSpec) = environment[argSpec.toEnvironmentName()]

    /**
     * Property names in environment variables are written in a specific way.
     * For example:
     * <code>--wallet-password</code> becomes <code>WEB3j_OPENAPI_WALLET_PASSWORD</code>
     * for non composed names: <code>--port</code> becomes <code>WEB3J_OPENAPI_PORT</code>
     */
    private fun CommandLine.Model.ArgSpec.toEnvironmentName(): String {
        return (this as CommandLine.Model.OptionSpec)
            .longestName()
            .toUpperCase()
            .removePrefix("--")
            .replace("-", "_")
            .prependIndent("WEB3J_OPENAPI_")
    }
}
