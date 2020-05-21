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
package org.web3j.openapi.codegen.servergen.subgenerators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import org.web3j.openapi.codegen.LICENSE
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.SolidityUtils
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File

class ResourcesImplsGenerator(
    val packageName: String,
    private val contractName: String,
    private val resourcesDefinition: List<AbiDefinition>,
    private val folderPath: String
) {

    fun generate() {
        generateClass().writeTo(File(folderPath))
        File(folderPath)
            .walkTopDown()
            .filter { file -> file.name.endsWith(".kt") }
            .forEach { file ->
                CopyUtils.kotlinFormat(file)
            }
    }

    private fun generateClass(): FileSpec {
        val resourcesFile = FileSpec.builder(
            "$packageName.server.${contractName.toLowerCase()}",
            "${contractName.capitalize()}ResourceImpl"
        )

        val contractClass = ClassName(
            "$packageName.wrappers",
            contractName.capitalize()
        )

        val constructorBuilder = FunSpec.constructorBuilder()
            .addParameter(
                contractName.decapitalize(),
                contractClass
            )

        val contractResourceClass = ClassName(
            "$packageName.core.${contractName.toLowerCase()}",
            "${contractName.capitalize()}Resource"
        )

        val resourcesClass = TypeSpec
            .classBuilder("${contractName.capitalize()}ResourceImpl")
            .primaryConstructor(constructorBuilder.build())
            .addProperty(
                PropertySpec.builder(
                    contractName.decapitalize(),
                    contractClass,
                    KModifier.PRIVATE
                )
                    .initializer(contractName.decapitalize())
                    .build()
            )
            .addSuperinterface(contractResourceClass)

        resourcesClass.addFunctions(generateFunctions())
        resourcesClass.addFunctions(generateEvents())

        return resourcesFile
            .addType(resourcesClass.build())
            .addComment(LICENSE)
            .build()
    }

    private fun generateEvents(): List<FunSpec> {
        val events = mutableListOf<FunSpec>()
        resourcesDefinition
            .filter { it.type == "event" }
            .forEach {
                val eventResponseClass =
                    ClassName("kotlin.collections", "List")
                        .plusParameter(
                            ClassName(
                                "$packageName.core.${contractName.toLowerCase()}.model",
                                "${it.name.capitalize()}EventResponse"
                            )
                        )
                val funSpec = FunSpec.builder("get${it.name.capitalize()}Event")
                    .returns(
                        eventResponseClass
                    )
                    .addModifiers(KModifier.OVERRIDE)

                funSpec.addParameter(
                    "transactionReceiptModel",
                    ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel")
                )
                    .addCode(
                        """
                                val eventResponse = ${contractName.decapitalize()}.get${it.name.capitalize()}Events(
                                    transactionReceiptModel.toTransactionReceipt())
                                return eventResponse.map{${it.name.capitalize()}EventResponse(${getEventResponseParameters(
                            it
                        )})}
                            """.trimIndent()
                    )
                events.add(funSpec.build())
            }
        return events
    }

    private fun getEventResponseParameters(abiDef: AbiDefinition): String {
        var params = ""
        abiDef.inputs.forEach {
            params += ", it.${it.name}"
        }
        return params.removePrefix(",")
    }

    private fun generateFunctions(): List<FunSpec> {
        val functions = mutableListOf<FunSpec>()
        resourcesDefinition
            .filter { it.type == "function" }
            .forEach {
                if (SolidityUtils.isFunctionDefinitionConstant(it) && it.outputs.isEmpty()) return@forEach
                val returnType = SolidityUtils.getFunctionReturnType(it)
                val funSpec = FunSpec.builder(it.name)
                    .returns(
                        returnType
                    )
                    .addModifiers(KModifier.OVERRIDE)
                val code = if (it.inputs.isEmpty()) {
                    "${contractName.decapitalize()}.${it.name}().send()"
                } else {
                    val nameClass = ClassName(
                        "$packageName.core.${contractName.toLowerCase()}.model",
                        "${it.name.capitalize()}Parameters"
                    )
                    funSpec.addParameter(
                        "${it.name.decapitalize()}Parameters",
                        nameClass
                    )
                    """
                        ${contractName.decapitalize()}.${it.name}(
                                ${getCallParameters(it.inputs, it.name)}
                            ).send()
                    """.trimIndent()
                }
                if (returnType != ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel"))
                    funSpec.addCode("return $code")
                else
                    funSpec.addCode("return TransactionReceiptModel($code)")
                functions.add(funSpec.build())
            }
        return functions
    }

    private fun getCallParameters(inputs: MutableList<AbiDefinition.NamedType>, functionName: String): String {
        var callParameters = ""
        inputs.forEachIndexed { index, input ->
            callParameters += "${functionName.decapitalize()}Parameters.${input.name ?: "input$index"},"
        }
        return callParameters.removeSuffix(",")
    }
}
