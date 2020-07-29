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
package org.web3j.openapi.server.console

import com.fasterxml.jackson.annotation.JsonProperty
import org.web3j.openapi.server.console.options.ConfigFileOptions
import org.web3j.openapi.server.console.options.ProjectOptions
import org.web3j.openapi.server.console.options.NetworkOptions
import org.web3j.openapi.server.console.options.ServerOptions
import org.web3j.openapi.server.console.options.CredentialsOptions
import picocli.CommandLine

class ConsoleConfiguration {

    @CommandLine.Mixin
    @JsonProperty("credentials")
    val credentialsOptions = CredentialsOptions()

    @CommandLine.Mixin
    @JsonProperty("server")
    val serverOptions = ServerOptions()

    @CommandLine.Mixin
    @JsonProperty("network")
    val networkOptions = NetworkOptions()

    @CommandLine.Mixin
    @JsonProperty("configFile")
    val configFileOptions = ConfigFileOptions()

    @CommandLine.Mixin
    @JsonProperty("project")
    val projectOptions = ProjectOptions()

    @CommandLine.Option(
        names = ["--contract-addresses"],
        description = ["Add pre-deployed contract addresses"],
        arity = "0..*",
        split = ",",
        required = false
    )
    var contractAddresses: Map<String, String>? = null
}
