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
import org.web3j.openapi.codegen.common.ContractResource
import org.web3j.openapi.codegen.common.Import
import org.web3j.openapi.codegen.config.ContractDetails
import org.web3j.openapi.codegen.utils.GeneratorUtils.functionName
import org.web3j.openapi.codegen.utils.TemplateUtils
import org.web3j.openapi.codegen.utils.extractStructs
import org.web3j.openapi.codegen.utils.getReturnType
import org.web3j.openapi.codegen.utils.isTransactional
import java.io.File
import java.nio.file.Path

internal class CoreApiGenerator(
    val packageName: String,
    val folderPath: String,
    val contractDetails: ContractDetails
) {
    private val context = mutableMapOf<String, Any>()

    init {
        context["packageName"] = packageName
        context["contractName"] = contractDetails.lowerCaseContractName
        context["contractNameCap"] = contractDetails.capitalizedContractName
        context["contractDetails"] = contractDetails
        context["imports"] = imports()
        context["contractResources"] = contractResources()
    }

    fun generate() {
        File(folderPath).apply {
            mkdirs()
        }
        copySources()
        generateEventsResources()
        generateModels()
        generateStructsModels()
    }

    private fun generateEventsResources() {
        contractDetails.abiDefinitions
            .filter { it.type == "event" }
            .forEach { abiDefinition ->
                context["eventName"] = abiDefinition.functionName()!!.capitalize()
                TemplateUtils.generateFromTemplate(
                    context = context,
                    outputDir = folderPath,
                    template = TemplateUtils.mustacheTemplate("core/src/api/NamedEventResource.mustache"),
                    name = "${abiDefinition.functionName()!!.capitalize()}EventResource.kt"
                )
            }
    }

    private fun generateStructsModels() {
        extractStructs(contractDetails.abiDefinitions)?.forEach { structDefinition ->
            CoreStructsModelGenerator(
                packageName = packageName,
                contractName = contractDetails.capitalizedContractName,
                functionName = structDefinition!!.internalType.split(".").last(),
                folderPath = Path.of(
                    folderPath.substringBefore("kotlin"),
                    "kotlin"
                ).toString(),
                components = structDefinition.components
            ).generate()
        }
    }

    private fun imports(): List<Import> {
        return contractDetails.abiDefinitions
            .filter { it.type == "function" && it.inputs.isNotEmpty() || it.type == "event" }
            .map {
                if (it.type == "function")
                    Import("import $packageName.core.${contractDetails.lowerCaseContractName}.model.${it.functionName()!!.capitalize()}Parameters")
                else
                    Import("import $packageName.core.${contractDetails.lowerCaseContractName}.model.${it.functionName()!!.capitalize()}EventResponse")
            }
    }

    private fun contractResources(): List<ContractResource> {
        val resources = mutableListOf<ContractResource>()
        contractDetails.abiDefinitions
            .filter { it.type == "function" || it.type == "event" }
            .forEach {
                if (it.type == "function") {
                    if (!it.isTransactional() && it.outputs.isEmpty()) return@forEach
                    val parameters =
                        if (it.inputs.isNotEmpty())
                            "${it.functionName()!!.decapitalize()}Parameters : ${it.functionName()!!.capitalize()}Parameters"
                        else ""
                    val operationTag = "@Operation(tags = [\"${contractDetails.capitalizedContractName}\"],  summary = \"Execute the ${it.functionName()!!.capitalize()} method\")"
                    resources.add(
                        ContractResource(
                            functionName = it.functionName()!!,
                            resource = "fun ${it.functionName()}($parameters)",
                            method = if (it.inputs.isEmpty()) "@GET" else "@POST",
                            returnType = it.getReturnType(packageName, contractDetails.lowerCaseContractName).toString(),
                            operationTag = operationTag,
                            mediaType = "@Produces(MediaType.APPLICATION_JSON)",
                            path = "@Path(\"${it.functionName()!!.capitalize()}\")"
                        )
                    )
                } else {
                    resources.add(
                        ContractResource(
                            functionName = "${it.functionName()}Event",
                            resource = "val ${it.functionName()!!.decapitalize()}Events",
                            method = "@get:Path(\"${it.functionName()!!.capitalize()}Events\")",
                            returnType = "${it.functionName()!!.capitalize()}EventResource"
                        )
                    )
                }
            }
        return resources
    }

    private fun generateModels() {
        contractDetails.abiDefinitions.forEach {
            logger.debug("Generating ${it.functionName(true)} model")

            when (it.type) {
                "constructor" -> {
                    if (it.inputs.isNotEmpty())
                        CoreDeployModelGenerator(
                            packageName = packageName,
                            contractName = contractDetails.capitalizedContractName,
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
                            contractName = contractDetails.capitalizedContractName,
                            functionName = it.functionName()!!,
                            folderPath = Path.of(
                                folderPath.substringBefore("kotlin"),
                                "kotlin"
                            ).toString(),
                            inputs = it.inputs
                        ).generate()
                }
                "event" -> {
                    CoreEventsModelGenerator(
                        packageName = packageName,
                        contractName = contractDetails.capitalizedContractName,
                        eventName = it.functionName()!!,
                        folderPath = Path.of(
                            folderPath.substringBefore("kotlin"),
                            "kotlin"
                        ).toString(),
                        outputs = it.inputs
                    ).generate()
                }
            }
        }
    }

    private fun copySources() {
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("core/src/api/ContractLifecycle.mustache"),
            name = "${contractDetails.capitalizedContractName}Lifecycle.kt"
        )
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("core/src/api/ContractResource.mustache"),
            name = "${contractDetails.capitalizedContractName}Resource.kt"
        )
    }

    companion object : KLogging()
}
