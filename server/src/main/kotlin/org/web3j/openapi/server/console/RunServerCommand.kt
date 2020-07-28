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

import org.web3j.abi.datatypes.Address
import org.web3j.openapi.server.OpenApiServer
import org.web3j.openapi.server.config.ContractAddresses
import org.web3j.openapi.server.config.OpenApiServerConfig
import org.web3j.openapi.server.console.options.ConfigFileOptions
import org.web3j.openapi.server.console.options.CredentialsOptions
import org.web3j.openapi.server.console.options.NetworkOptions
import org.web3j.openapi.server.console.options.ProjectOptions
import org.web3j.openapi.server.console.options.ServerOptions
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.ExitCode
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option
import java.io.File
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(
    showDefaultValues = true,
    mixinStandardHelpOptions = true,
    description = ["Runs a Web3j OpenAPI server."],
    version = ["1.0"] // TODO: Make version not hardcoded
)
class RunServerCommand : Callable<Int> {

    @Option(
        names = ["--contract-addresses"],
        description = ["Add pre-deployed contract addresses"],
        arity = "0..*",
        split = ",",
        required = false
    )
    private var contractAddresses: Map<String, String>? = null

    @Mixin
    private val credentials = CredentialsOptions()

    @Mixin
    private val serverOptions = ServerOptions()

    @CommandLine.ArgGroup
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
            projectName = projectOptions.projectName,
            network = networkOptions.network
            contractAddresses = ContractAddresses().apply {
                contractAddresses?.let {
                    putAll(it.mapValues { Address(it.value) })
                }
            }
        )
    }

    companion object {
        private const val DEFAULT_FILE_PATH = "~/.epirus/web3j.openapi.properties"
        private const val CONFIG_FILE_ENV_NAME = "WEB3J_OPENAPI_CONFIG_FILE"

        private val environment = System.getenv()

        @JvmStatic
        fun main(args: Array<String>) {
            val runServerCommand = CommandLine(RunServerCommand())

            configureDefaultProvider(args, runServerCommand)
            runServerCommand.execute(*args).apply { exitProcess(this) }
        }

        private fun configureDefaultProvider(args: Array<String>, commandLine: CommandLine) {

            // First pass to get the configuration file
            val configFileCommand = ConfigFileCommand()
            val configFileCommandLine = CommandLine(configFileCommand).apply {
                parseArgs(*args.drop(1).toTypedArray())
            }

            if (configFileCommandLine.isUsageHelpRequested) {
                commandLine.run {
                    usage(System.out)
                    exitProcess(ExitCode.OK)
                }
            } else if (configFileCommandLine.isVersionHelpRequested) {
                commandLine.run {
                    printVersionHelp(System.out)
                    exitProcess(ExitCode.OK)
                }
            }

            val configFile = configFileCommand.configFileOptions.configFile
                ?: environment[CONFIG_FILE_ENV_NAME]?.run { File(this) }

            commandLine.defaultValueProvider =
                ConfigDefaultProvider(configFile, environment, File(DEFAULT_FILE_PATH))
        }
    }
}
