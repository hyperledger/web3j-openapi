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

import org.web3j.openapi.codegen.GenerateOpenApi
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.GeneratorUtils.loadContractConfigurations
import org.web3j.openapi.console.utils.GradleUtils.runGradleTask
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.nio.file.Path
import java.util.concurrent.Callable

@Command(
    name = "generate",
    showDefaultValues = true,
    description = ["Generates a web3j-openapi project"]
)
class GenerateCommand : Callable<Int> {

    // TODO: Add logs level specification
//    @Option(
//        names = ["-l", "--logging"],
//        converter = [LogTypeConverter::class],
//        paramLabel = "<LOG VERBOSITY LEVEL>",
//        description = ["Logging verbosity levels: OFF, FATAL, WARN, INFO, DEBUG, TRACE, ALL (default: INFO)."],
//        arity = "1"
//    )
//    private var logLevel: Level? = null

    @Option(
        names = ["-o", "--output"],
        description = ["specify the output directory."],
        defaultValue = "."
    )
    private lateinit var outputDirectory: File

    @Option(
        names = ["-a", "--abi"],
        description = ["specify the abi files and folders."],
        arity = "1..*",
        required = true
    )
    private lateinit var abis: List<File>

    @Option(
        names = ["-b", "--bin"],
        description = ["specify the bin."],
        arity = "1..*",
        required = true
    )
    private lateinit var bins: List<File>

    @Option(
        names = ["-n", "--project-name"],
        description = ["specify the project name."],
        required = true
    )
    private lateinit var projectName: String

    @Option(
        names = ["-p", "--package-name"],
        description = ["specify the package name."],
        required = true
    )
    private lateinit var packageName: String

    @Option(
        names = ["--server"],
        description = ["set to false to only generate the core interfaces of the OpenAPI."],
        defaultValue = "true"
    )
    private var isServerGenerated: Boolean = true

    @Option(
        names = ["--dev"],
        description = ["not delete the failed build files."],
        defaultValue = "false"
    )
    private var dev: Boolean = false

    @Option(
        names = ["--jar"],
        description = ["set to true to generate the JAR only."],
        defaultValue = "false"
    )
    private var isJarOnly: Boolean = false

    @Option(
        names = ["--swagger-ui"],
        description = ["set to false to ignore the generation of the Swagger UI."],
        defaultValue = "true"
    )
    private var swagger: Boolean = true

    @Option(
        names = ["--address-length"],
        description = ["specify the address length."],
        defaultValue = "20"
    )
    private var addressLength: Int = 20

    override fun call(): Int {
        val projectFolder = Path.of(
            outputDirectory.canonicalPath,
            projectName
        ).toFile().apply {
            deleteRecursively()
            mkdirs()
        }

        return try {
            generate(projectFolder)
            0
        } catch (e: Exception) {
            if (!dev) projectFolder.deleteRecursively()
            println(e.message)
            1
        }
    }

    private fun generate(projectFolder: File) {

        val generatorConfiguration = GeneratorConfiguration(
            projectName = projectName,
            packageName = packageName,
            outputDir = projectFolder.path,
            jarDir = outputDirectory,
            contracts = loadContractConfigurations(abis, bins),
            addressLength = addressLength
        )

        if (isServerGenerated) {
            GenerateOpenApi(generatorConfiguration).generateAll()
            if (swagger) {
                runGradleTask(projectFolder, "resolve", "Generating OpenAPI specs...", null)
                runGradleTask(projectFolder, "generateSwaggerUI", "Generating SwaggerUI...", null)
                runGradleTask(projectFolder, "moveSwaggerUiToResources", "Setting up the SwaggerUI...", null)
            }

            runGradleTask(
                projectFolder,
                "shadowJar",
                "Generating the fat JAR to ${projectFolder.parentFile.canonicalPath}...",
                null
            )
            runGradleTask(projectFolder, "clean", "Cleaning up")

            if (isJarOnly) {
                projectFolder.deleteRecursively()
            }
        } else {
            print("Generating Core interfaces...")
            GenerateOpenApi(generatorConfiguration).generateCore()
            print(" Done.")
        }

        println("Done.")
    }
}
