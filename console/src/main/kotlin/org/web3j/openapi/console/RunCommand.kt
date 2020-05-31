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
import org.web3j.openapi.console.options.ServerOptions
import org.web3j.openapi.server.OpenApiServer
import org.web3j.openapi.server.config.OpenApiServerConfig
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option
import java.io.File
import java.io.PrintWriter
import java.util.concurrent.Callable

@Command(
    mixinStandardHelpOptions = true,
    version = ["1.0"] // TODO: Make version not hardcoded
)
class RunCommand(
    private val outputWriter: PrintWriter,
    private val environment: Map<String, String>
) : Callable<Int> {

    @Mixin
    private val credentials = CredentialsOptions()

    @Mixin
    private val serverOptions = ServerOptions()

    @Mixin
    private val networkOptions = NetworkOptions()

    @Mixin
    private val configFileOptions = ConfigFileOptions()

    @Option(
        names = ["-n", "--name"],
        description = ["specify the project name"],
        defaultValue = "Web3j-OpenAPI"
    )
    private lateinit var projectName: String

    override fun call(): Int {
        val serverConfig = serverConfig()

        OpenApiServer(serverConfig).apply {
            return try {
                start()
                join()
                0
            } catch (t: Throwable) {
                1
            } finally {
                destroy()
            }
        }
    }

    // TODO Move to a custom execution strategy
    fun parse(vararg args: String): Int {
        val serverCommand = CommandLine(this)

        // First pass to get the configuration file
        val configFileCommand = ConfigFileCommand()
        val configFileCommandLine = CommandLine(configFileCommand).apply {
            parseArgs(*args)
        }

        if (configFileCommandLine.isUsageHelpRequested) {
            return serverCommand.run {
                printVersionHelp(outputWriter)
                commandSpec.exitCodeOnVersionHelp()
            }
        } else if (configFileCommandLine.isVersionHelpRequested) {
            return serverCommand.run {
                usage(outputWriter)
                commandSpec.exitCodeOnUsageHelp()
            }
        }

        // final pass
        return serverCommand.run {
            val configFile = configFileCommand.configFileOptions.configFile
                ?: environment[CONFIG_FILE_ENV_NAME]?.run { File(this) }

            defaultValueProvider = ConfigDefaultProvider(configFile, environment, File(DEFAULT_FILE_PATH))
            execute(*args)
        }
    }

    private fun serverConfig(): OpenApiServerConfig {
        return OpenApiServerConfig(
            host = serverOptions.host.hostName,
            port = serverOptions.port,
            nodeEndpoint = networkOptions.endpoint,
            privateKey = credentials.privateKey,
            walletFilePath = if (credentials.walletOptions.isWalletFileInitialized())
                credentials.walletOptions.walletFile.canonicalPath else "",
            walletPassword = credentials.walletOptions.walletPassword,
            projectName = projectName
        )
    }

    companion object {
        private const val DEFAULT_FILE_PATH = "~/.epirus/web3j.openapi.properties"
        private const val CONFIG_FILE_ENV_NAME = "WEB3J_OPENAPI_CONFIG_FILE"
    }
}
