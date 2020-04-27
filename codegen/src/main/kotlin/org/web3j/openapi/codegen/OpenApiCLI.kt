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
package org.web3j.openapi.codegen

import org.web3j.openapi.codegen.config.ContractConfiguration
import org.web3j.openapi.codegen.config.ContractDetails
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.SolidityUtils
import picocli.CommandLine
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.concurrent.Callable

@CommandLine.Command(name = "generate-openapi",
    description = ["Generates a web3j-openapi project"])
class OpenApiCLI : Callable<Int> {

    @CommandLine.Option(names = ["-o", "--output"],
        description = ["specify the output directory."],
        defaultValue = ".")
    var outputDirectory: String = "."

    @CommandLine.Option(names = ["-a", "--abi"],
        description = ["specify the abi files and folders."],
        arity = "1..*",
        required = true)
    lateinit var abis: List<String>

    @CommandLine.Option(names = ["-b", "--bin"],
        description = ["specify the bin."],
        arity = "1..*",
        required = true)
    lateinit var bins: List<String>

    @CommandLine.Option(names = ["-n", "--project-name"],
        description = ["specify the project name."],
        required = true)
    lateinit var projectName: String

    @CommandLine.Option(names = ["-e", "--node-endpoint"],
        description = ["specify the node endpoint."],
        defaultValue = "",
        required = true)
    lateinit var nodeEndpoint: String

    @CommandLine.Option(names = ["-k", "--private-key"],
        description = ["specify the private key to use in hex format."],
        defaultValue = "",
        required = true)
    lateinit var pkey: String

    @CommandLine.Option(names = ["-p", "--package-name"],
        description = ["specify the package name."],
        required = true)
    lateinit var packageName: String

    @CommandLine.Option(names = ["-c", "--config"],
        description = ["specify the openapi configuration json."],
        defaultValue = ".")
    var config: String? = null

    // TODO: Add possibility to generate only specific modules. eg: GenerateOpenApi(...).generateCore() etc

    override fun call(): Int {
        val output = File(
            Path.of(
            outputDirectory,
            projectName
        ).toString())
        output.mkdirs()

        val generatorConfiguration = GeneratorConfiguration(
            projectName = projectName,
            packageName = packageName,
            outputDir = output.path,
            contracts = getContractsConfiguration(),
            privateKey = "0x${pkey.removePrefix("0x")}",
            endpoint = nodeEndpoint
        )

        GenerateOpenApi(generatorConfiguration).generateAll()
        return 0
    }

    private fun getContractsConfiguration(): List<ContractConfiguration> {
        abis = recurseIntoFolders(abis, ".abi")
        bins = recurseIntoFolders(bins, ".bin")
        val contractsConfig = mutableListOf<ContractConfiguration>()
        abis.forEach {
            val abi = File(it)
            val bin = bins.find { bin ->
                bin.endsWith("${abi.name.removeSuffix(".abi")}.bin")
            } ?: throw FileNotFoundException("${abi.name.removeSuffix(".abi")}.bin")
            contractsConfig.add(
                ContractConfiguration(
                    abi,
                    File(bin),
                    ContractDetails(
                        abi.name.removeSuffix(".abi"),
                        SolidityUtils.loadContractDefinition(abi) // TODO: Use the web3j.codegen function
                    )
                )
            )
        }
        return contractsConfig
    }

    private fun recurseIntoFolders(list: List<String>, extension: String) : List<String>{
        val recs = mutableListOf<String>()
        list
            .filter { it.endsWith(extension) || File(it).isDirectory }
            .forEach {
                val currentFile = File(it)
                if (currentFile.isFile) recs.add(currentFile.path)
                else currentFile.listFiles()
                    .filter { it.name.endsWith(extension) || it.isDirectory }
                    .forEach {file ->
                        if(file.isFile) recs.add(file.path)
                        else recs.addAll(
                            recurseIntoFolders(
                                file.listFiles().map { it.path }
                                , extension
                            )
                        )
                    }
            }
        return recs
    }
}
