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
import org.web3j.openapi.server.console.defaultproviders.CascadingDefaultProvider
import org.web3j.openapi.server.console.defaultproviders.EnvironmentVariableDefaultProvider
import org.web3j.openapi.server.console.defaultproviders.JavaPropDefaultProvider
import org.web3j.openapi.server.console.defaultproviders.JsonDefaultProvider
import org.web3j.openapi.server.console.defaultproviders.YamlDefaultProvider
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.ExitCode
import picocli.CommandLine.Mixin
import java.io.File
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(
    showDefaultValues = true,
    mixinStandardHelpOptions = true,
    description = ["Runs a Web3j OpenAPI server."],
    version = ["1.0"], // TODO: Make version not hardcoded
)
class RunServerCommand : Callable<Int> {

    @Mixin
    private val consoleConfiguration = ConsoleConfiguration()

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
            host = consoleConfiguration.serverOptions.host.hostName,
            port = consoleConfiguration.serverOptions.port,
            nodeEndpoint = consoleConfiguration.networkOptions.endpoint,
            privateKey = consoleConfiguration.credentialsOptions.privateKey,
            walletFile = consoleConfiguration.credentialsOptions.walletOptions.walletFile,
            walletPassword = consoleConfiguration.credentialsOptions.walletOptions.walletPassword,
            projectName = consoleConfiguration.projectOptions.projectName,
            contractAddresses = ContractAddresses().apply {
                consoleConfiguration.contractAddresses?.let { contractAddresses ->
                    putAll(
                        contractAddresses
                            .mapKeys { it.key.lowercase() }
                            .mapValues { Address(it.value) },
                    )
                }
            },
        )
    }

    companion object {
        private val DEFAULT_FILE_PATH_WITHOUT_EXTENSION = "${System.getProperty("user.home")}/.epirus/web3j.openapi"
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

            val defaultProvidersList = mutableListOf<CommandLine.IDefaultValueProvider>()
            when (configFile?.extension) {
                "yaml" -> YamlDefaultProvider(configFile)
                "json" -> JsonDefaultProvider(configFile)
                "properties" -> JavaPropDefaultProvider(configFile)
                else -> null
            }?.let { defaultProvidersList.add(it) }

            when {
                File("$DEFAULT_FILE_PATH_WITHOUT_EXTENSION.yaml").exists() ->
                    YamlDefaultProvider(File("$DEFAULT_FILE_PATH_WITHOUT_EXTENSION.yaml"))
                File("$DEFAULT_FILE_PATH_WITHOUT_EXTENSION.json").exists() ->
                    JsonDefaultProvider(File("$DEFAULT_FILE_PATH_WITHOUT_EXTENSION.json"))
                File("$DEFAULT_FILE_PATH_WITHOUT_EXTENSION.properties").exists() ->
                    JavaPropDefaultProvider(File("$DEFAULT_FILE_PATH_WITHOUT_EXTENSION.properties"))
                else -> null
            }?.let { defaultProvidersList.add(it) }

            defaultProvidersList.add(EnvironmentVariableDefaultProvider(environment))

            commandLine.defaultValueProvider = CascadingDefaultProvider(*defaultProvidersList.toTypedArray())
        }
    }
}
