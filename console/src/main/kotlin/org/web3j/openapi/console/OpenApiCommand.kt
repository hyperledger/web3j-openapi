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

import org.web3j.openapi.console.OpenApiCommand.VersionProvider
import picocli.CommandLine
import picocli.CommandLine.IVersionProvider
import picocli.CommandLine.Model.CommandSpec
import picocli.CommandLine.ParameterException
import picocli.CommandLine.Spec
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Properties
import java.util.concurrent.Callable
import picocli.CommandLine.Command
import java.time.OffsetDateTime

@Command(
    name = "openapi",
    description = ["web3j-openapi cli"],
    versionProvider = VersionProvider::class,
    subcommands = [GenerateCommand::class],
    mixinStandardHelpOptions = true
)
class OpenApiCommand : Callable<Int> {

    @Spec
    private lateinit var spec: CommandSpec

    override fun call(): Int {
        throw ParameterException(spec.commandLine(), "Missing required sub-command (see below)")
    }

    object VersionProvider : IVersionProvider {

        val versionName: String
        val buildTimestamp: OffsetDateTime

        private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS O")

        init {
            val url = javaClass.classLoader.getResource("version.properties")
                ?: throw IllegalStateException("No version.properties file found in the classpath.")

            val properties = Properties().apply { load(url.openStream()) }

            versionName = properties.getProperty("version")
            buildTimestamp = properties.getProperty("timestamp").toLong().let {
                Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC)
            }
        }

        override fun getVersion(): Array<String> {
            return arrayOf(
                "Version: $versionName",
                "Build timestamp: ${buildTimestamp.let { timeFormatter.format(it) }}"
            )
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            CommandLine(OpenApiCommand()).execute(*args)
        }
    }
}
