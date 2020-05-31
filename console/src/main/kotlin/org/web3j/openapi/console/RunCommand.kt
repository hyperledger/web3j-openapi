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

import org.web3j.openapi.console.options.ConfigFileOptions
import org.web3j.openapi.console.options.CredentialsOptions
import org.web3j.openapi.console.options.NetworkOptions
import org.web3j.openapi.console.options.ProjectOptions
import org.web3j.openapi.console.options.ServerOptions
import org.web3j.openapi.server.OpenApiServer
import org.web3j.openapi.server.config.OpenApiServerConfig
import picocli.CommandLine.Command
import picocli.CommandLine.ExitCode
import picocli.CommandLine.Mixin
import java.util.concurrent.Callable

@Command(
    name = "run",
    showDefaultValues = true,
    mixinStandardHelpOptions = true,
    description = ["Runs a Web3j OpenAPI server."],
    version = ["1.0"] // TODO: Make version not hardcoded
)
class RunCommand : Callable<Int> {

    @Mixin
    private val credentials = CredentialsOptions()

    @Mixin
    private val serverOptions = ServerOptions()

    @Mixin
    private val networkOptions = NetworkOptions()

    @Mixin
    private val configFileOptions = ConfigFileOptions()

    @Mixin
    private val projectOptions = ProjectOptions()

    override fun call(): Int {
        val serverConfig = serverConfig()

        OpenApiServer(serverConfig).apply {
            return try {
                start()
                join()
                ExitCode.OK
            } catch (t: Throwable) {
                ExitCode.SOFTWARE
            } finally {
                destroy()
            }
        }
    }

    private fun serverConfig(): OpenApiServerConfig {
        return OpenApiServerConfig(
            host = serverOptions.host.hostName,
            port = serverOptions.port,
            nodeEndpoint = networkOptions.endpoint,
            privateKey = credentials.privateKey,
            walletFile = credentials.walletOptions.walletFile,
            walletPassword = credentials.walletOptions.walletPassword,
            projectName = projectOptions.projectName
        )
    }
}
