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
package org.web3j.openapi.codegen.coregen.subgenerators

import mu.KLogging
import org.web3j.openapi.codegen.config.ContractDetails
import org.web3j.openapi.codegen.common.ContractResource
import org.web3j.openapi.codegen.common.Import
import org.web3j.openapi.codegen.utils.SolidityUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File
import java.nio.file.Path

class CoreApiGenerator(
    val packageName: String,
    val folderPath: String,
    val contractDetails: ContractDetails
) {
    val context = mutableMapOf<String, Any>()

    init {
        context["packageName"] = packageName
        context["contractName"] = contractDetails.lowerCaseContractName()
        context["contractDetails"] = contractDetails
        context["imports"] = imports()
        context["contractResources"] = contractResources()
    }

    fun generate() {
        File(folderPath).apply {
            mkdirs()
        }
        copySources()
        generateModels()
    }

    private fun imports(): List<Import> {
        return contractDetails.functionsDefintion
            .filter { it.type == "function" && it.inputs.isNotEmpty() }
            .map {
                Import("import $packageName.core.${contractDetails.lowerCaseContractName()}.model.${it.name.capitalize()}Parameters")
            }
    }

    private fun contractResources(): List<ContractResource> {
        val resources = mutableListOf<ContractResource>()
        contractDetails.functionsDefintion
            .filter { it.type == "function" }
            .forEach {
                val parameters =
                    if (it.inputs.isNotEmpty())
                        "${it.name.decapitalize()}Parameters : ${it.name.capitalize()}Parameters"
                    else ""
                resources.add(
                    ContractResource(
                        it.name.capitalize(),
                        "fun ${it.name}($parameters)",
                        if (it.inputs.isEmpty()) "GET" else "POST",
                        getFunctionReturnType(it)
                    )
                )
            }
        return resources
    }

    private fun getFunctionReturnType(it: AbiDefinition): String {
        // TODO: Check outputs and return the corresponding type
        return if(it.inputs.isEmpty() && it.name == "kill")
                "String"
        else "TransactionReceipt"
    }

    private fun generateModels() {
        contractDetails.functionsDefintion.forEach {
            logger.debug("Generating ${it.name} model")

            when (it.type) {
                "constructor" -> {
                    if (it.inputs.isNotEmpty())
                        CoreDeployModelGenerator(
                            packageName = packageName,
                            contractName = contractDetails.capitalizedContractName(),
                            folderPath = Path.of(
                                folderPath.substringBefore("kotlin"),
                                "kotlin"
                            ).toString(),
                            inputs = it.inputs
                        ).generate()
                }
                "function" -> {
                    if (it.inputs.isNotEmpty())
                        CoreFunctionsModelGenerator(
                            packageName = packageName,
                            contractName = contractDetails.capitalizedContractName(),
                            functionName = it.name,
                            folderPath = Path.of(
                                folderPath.substringBefore("kotlin"),
                                "kotlin"
                            ).toString(),
                            inputs = it.inputs
                        ).generate()
                }
                else -> println("Unsupported type of abi types") // TODO: Create corresponding exception
            }
        }
    }

    private fun copySources() {
        File("codegen/src/main/resources/core/src/api")
            .listFiles()
            ?.forEach {
                logger.debug("Generating from ${it.canonicalPath}")
                TemplateUtils.generateFromTemplate(
                    context = context,
                    outputDir = folderPath,
                    template = TemplateUtils.mustacheTemplate(it.path.substringAfter("resources/")),
                    name = "${contractDetails.capitalizedContractName()}${it.name.removeSuffix(".mustache")
                        .removePrefix("Contract")}.kt"
                )
            }
    }

    companion object : KLogging()
}
