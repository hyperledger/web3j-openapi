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

import mu.KLogging
import org.web3j.openapi.codegen.GenerateOpenApi
import org.web3j.openapi.codegen.config.ContractConfiguration
import org.web3j.openapi.codegen.config.ContractDetails
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.SolidityUtils
import org.web3j.openapi.console.utils.GradleUtils.runGradleTask
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.concurrent.Callable

@Command(
    name = "generate",
    description = ["Generates a web3j-openapi project"]
)
class GenerateCmd : Callable<Int> {

    // TODO: Add logs level specification
//    @Option(
//        names = ["-l", "--logging"],
//        converter = [LogTypeConverter::class],
//        paramLabel = "<LOG VERBOSITY LEVEL>",
//        description = ["Logging verbosity levels: OFF, FATAL, WARN, INFO, DEBUG, TRACE, ALL (default: INFO)."],
//        arity = "1"
//    )
//    private var logLevel: Level? = null

    @Option(names = ["-o", "--output"],
        description = ["specify the output directory."],
        defaultValue = ".")
    var outputDirectory: String = "."

    @Option(names = ["-a", "--abi"],
        description = ["specify the abi files and folders."],
        arity = "1..*",
        required = true)
    lateinit var abis: List<String>

    @Option(names = ["-b", "--bin"],
        description = ["specify the bin."],
        arity = "1..*",
        required = true)
    lateinit var bins: List<String>

    @Option(names = ["-n", "--project-name"],
        description = ["specify the project name."],
        required = true)
    lateinit var projectName: String

    @Option(names = ["-p", "--package-name"],
        description = ["specify the package name."],
        required = true)
    lateinit var packageName: String

    @Option(names = ["--core"],
        description = ["only generate the core interfaces of the OpenAPI."],
        defaultValue = "false")
    var core: Boolean = false

    @Option(names = ["--dev"],
        description = ["not delete the failed build files."],
        defaultValue = "false")
    var dev: Boolean = false

    override fun call(): Int {
        val projectFolder = File(
            Path.of(
                outputDirectory,
                projectName
            ).toString()
        ).apply {
            deleteRecursively()
            mkdirs()
        }

        try {
            generate(projectFolder)
        } catch (e: Exception) {
            if (!dev) projectFolder.deleteRecursively()
            throw e
        }
        return 0
    }

    private fun generate(projectFolder: File): Int {

        val generatorConfiguration = GeneratorConfiguration(
            projectName = projectName,
            packageName = packageName,
            outputDir = projectFolder.path,
            jarDir = outputDirectory,
            contracts = getContractsConfiguration()
        )

        if (core) {
            println("Generating Core interfaces")
            generatorConfiguration.onlyCore = true
            GenerateOpenApi(generatorConfiguration).generateCore()
        } else {
            GenerateOpenApi(generatorConfiguration).generateAll()
            runGradleTask(projectFolder, "resolve", "Generating OpenAPI specs")
            runGradleTask(projectFolder, "generateSwaggerUI", "Generating SwaggerUI")
            runGradleTask(projectFolder, "moveSwaggerUiToResources", "Setting up the SwaggerUI")

            runGradleTask(projectFolder, "shadowJar", "Generating the FatJar to ${projectFolder.parentFile.canonicalPath}")

            runGradleTask(projectFolder, "clean", "Cleaning up")
        }

        println("Done.")
        return 0
    }

    private fun getContractsConfiguration(): List<ContractConfiguration> {
        abis = recurseIntoFolders(abis, ".abi")
        bins = recurseIntoFolders(bins, ".bin")
        val contractsConfig = mutableListOf<ContractConfiguration>()
        abis.forEach { abi ->
            val abiFile = File(abi)
            val bin = bins.find { bin ->
                bin.endsWith("${abiFile.name.removeSuffix(".abi")}.bin")
            } ?: throw FileNotFoundException("${abiFile.name.removeSuffix(".abi")}.bin")
            contractsConfig.add(
                ContractConfiguration(
                    abiFile,
                    File(bin),
                    ContractDetails(
                        abiFile.name.removeSuffix(".abi"),
                        SolidityUtils.loadContractDefinition(abiFile) // TODO: Use the web3j.codegen function
                    )
                )
            )
        }
        return contractsConfig
    }

    private fun recurseIntoFolders(list: List<String>, extension: String): List<String> {
        val recs = mutableListOf<String>()
        list
            .filter { it.endsWith(extension) || File(it).isDirectory }
            .forEach { filePath ->
                val currentFile = File(filePath)
                if (currentFile.isFile) recs.add(currentFile.path)
                else currentFile.listFiles()
                    .filter { file ->
                        file.name.endsWith(extension) || file.isDirectory
                    }
                    .forEach { file ->
                        if (file.isFile) recs.add(file.path)
                        else recs.addAll(
                            recurseIntoFolders(
                                file.listFiles().map { it.path }, extension
                            )
                        )
                    }
            }
        return recs
    }

    companion object : KLogging()
}
