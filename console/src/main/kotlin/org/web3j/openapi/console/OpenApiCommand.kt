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

import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.ExitCode.OK
import java.io.File
import kotlin.system.exitProcess

@Command(
    name = "openapi",
//    versionProvider =  TODO: get the version from the properties (check web3j-corda project)
    description = ["web3j-openapi cli"],
    subcommands = [GenerateCommand::class, RunCommand::class],
    version = ["1.0"],
    mixinStandardHelpOptions = true
)
class OpenApiCommand {

    companion object {
        private const val DEFAULT_FILE_PATH = "~/.epirus/web3j.openapi.properties"
        private const val CONFIG_FILE_ENV_NAME = "WEB3J_OPENAPI_CONFIG_FILE"

        private val environment = System.getenv()

        @JvmStatic
        fun main(args: Array<String>) {
            val openApiCommand = CommandLine(OpenApiCommand())

            if (args.isNotEmpty() && args[0] == "run") {
                // We need a double-pass trick for default values
                configureDefaultProvider(args, openApiCommand)
            }

            openApiCommand.execute(*args).apply { exitProcess(this) }
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
                    exitProcess(OK)
                }
            } else if (configFileCommandLine.isVersionHelpRequested) {
                commandLine.run {
                    printVersionHelp(System.out)
                    exitProcess(OK)
                }
            }

            val configFile = configFileCommand.configFileOptions.configFile
                ?: environment[CONFIG_FILE_ENV_NAME]?.run { File(this) }

            commandLine.defaultValueProvider =
                ConfigDefaultProvider(configFile, environment, File(DEFAULT_FILE_PATH))
        }
    }
}
