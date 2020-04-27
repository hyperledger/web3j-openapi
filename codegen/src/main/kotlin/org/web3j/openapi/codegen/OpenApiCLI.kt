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
import java.util.concurrent.Callable

@CommandLine.Command(name = "openapi",
    description = ["Generates a web3j-openapi project"])
class OpenApiCLI : Callable<Int> {

    @CommandLine.Option(names = ["-o", "--output"],
        description = ["specify the output directory."],
        defaultValue = ".")
    var outputDirectory: String = "."

    @CommandLine.Option(names = ["-a", "--abi"],
        description = ["specify the abi files and folders."],
        required = true)
    lateinit var abi: String

    @CommandLine.Option(names = ["-b", "--bin"],
        description = ["specify the bin."],
        required = true)
    lateinit var bin: String

    @CommandLine.Option(names = ["-n", "--project-name"],
        description = ["specify the project name."],
        required = true)
    lateinit var projectName: String

    @CommandLine.Option(names = ["-e", "--node-endpoint"],
        description = ["specify the node endpoint."],
        defaultValue = "")
    var nodeEndpoint: String = ""

//    @CommandLine.Option(names = ["-c", "--credentials"],
//        description = ["specify the credentials."],
//        defaultValue = "")
//    var creds: String = ""

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

        val generatorConfiguration = GeneratorConfiguration(
            projectName = projectName,
            packageName = packageName,
            outputDir = outputDirectory,
            contracts = getContractsConfiguration()
        )

        GenerateOpenApi(generatorConfiguration).generateAll()
        return 0
    }

    private fun getContractsConfiguration(): List<ContractConfiguration> {
        val abiFile = File(abi)
        return listOf(
            ContractConfiguration(
                abiFile,
                File(bin),
                ContractDetails(
                    abiFile.name.removeSuffix(".abi"),
                    SolidityUtils.loadContractDefinition(abiFile) // TODO: Use the web3j.codegen function
                )
            )
        )
        // TODO: Add possibility to specify many files and folders and create configuration from them

//        abi.forEach {
//            val file = File(it)
//            if(file.isFile) {
//                ContractConfiguration(
//
//                )
//            }
//        }
    }
}
