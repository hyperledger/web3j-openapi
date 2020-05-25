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

import org.web3j.openapi.server.OpenApiServer
import org.web3j.openapi.server.cli.options.CredentialsOptions
import org.web3j.openapi.server.cli.options.NetworksOptions
import org.web3j.openapi.server.cli.options.ServerOptions
import org.web3j.openapi.server.config.OpenApiServerConfig
import org.web3j.openapi.server.config.OpenApiServerConfigBuilder
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option
import java.io.File
import java.util.Optional
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(
    defaultValueProvider = ConfigDefaultProvider::class
)
class ConfigCLI : Callable<Int> {

    private val environment = System.getenv()
    private val DEFAULT_FILE_PATH = "~/.epirus/web3j.openapi.properties"
    private val CONFIG_FILE_ENV_NAME = "WEB3J_OPENAPI_CONFIG_FILE"

    @Mixin
    private val credentials = CredentialsOptions()
    @Mixin
    private val serverOptions = ServerOptions()
    @Mixin
    private val networksOptions = NetworksOptions()

    @Option(
        names = ["-n", "--name"],
        description = ["specify the project name"],
        defaultValue = "Web3j-OpenAPI"
    )
    lateinit var projectName: String

    @Option(
        names = ["-c", "--config-file"],
        paramLabel = "<FILENAME>",
        description = ["Path/filename of the yaml config file (default: none)"],
        arity = "1"
    )
    private var configFile: File? = null

    override fun call(): Int {
        val serverConfig = serverConfig()

        OpenApiServer(serverConfig).apply {
            try {
                start()
                join()
            } catch (ex: Exception) {
                exitProcess(1)
            } finally {
                destroy()
            }
        }

        return 0
    }

    fun parse(vararg args: String): Int {
        // First pass to get the configuration file
        val configFileCommand = ConfigFileCommand()
        val configFileCommandLine = CommandLine(configFileCommand)
        configFileCommandLine.parseArgs(*args)
        if (configFileCommandLine.isUsageHelpRequested) {
            return executeCommandUsageHelp()
        } else if (configFileCommandLine.isVersionHelpRequested) {
            return executeCommandVersion()
        }

        val configFile = getConfigFileFromCliOrEnv(configFileCommand)

        // final pass
        val configCommandLine = CommandLine(this)
        configCommandLine.defaultValueProvider = ConfigDefaultProvider(configFile, environment, File(DEFAULT_FILE_PATH))
        return configCommandLine.execute(*args)
    }

    private fun getConfigFileFromCliOrEnv(configFileCommand: ConfigFileCommand): Optional<File> {
        return Optional.ofNullable<File>(configFileCommand.configFile)
            .or {
                Optional.ofNullable<String>(environment[CONFIG_FILE_ENV_NAME])
                    .map { pathname -> File(pathname) }
            }
    }

    private fun executeCommandVersion(): Int {
        val configCommandLine = CommandLine(this)
        return configCommandLine.commandSpec.exitCodeOnVersionHelp()
    }

    private fun executeCommandUsageHelp(): Int {
        val configCommandLine = CommandLine(this)
        return configCommandLine.commandSpec.exitCodeOnUsageHelp()
    }

    private fun serverConfig(): OpenApiServerConfig {
        return OpenApiServerConfigBuilder()
            .setHost(serverOptions.host.hostName)
            .setPort(8080)
            .setNodeEndpoint(networksOptions.endpoint)
            .setPrivateKey(credentials.privateKey)
            .setWalletFilePath(credentials.walletOptions.walletFile.canonicalPath)
            .setWalletPassword(credentials.walletOptions.walletPassword)
            .setProjectName(projectName)
            .build()
    }
}
