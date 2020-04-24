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
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asTypeName
import org.web3j.openapi.codegen.LICENSE
import org.web3j.openapi.codegen.utils.SolidityUtils
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.io.File

class ResourcesImplsGenerator(
    val packageName: String,
    private val contractName: String,
    private val functionsDefinition: List<AbiDefinition>,
    private val folderPath: String
) {

    fun generate() {
        generateClass().writeTo(File(folderPath))
    }

    private fun generateClass(): FileSpec {
        val resourcesFile = FileSpec.builder(
            "$packageName.server.${contractName.decapitalize()}",
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
            "$packageName.core.${contractName.decapitalize()}",
            "${contractName.capitalize()}Resource")

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

        generateFunctions()
            .forEach {
                resourcesClass.addFunction(it)
            }

        return resourcesFile
            .addType(resourcesClass.build())
            .addComment(LICENSE)
            .build()
    }

    private fun generateFunctions(): List<FunSpec> {
        val functions = mutableListOf<FunSpec>()
        functionsDefinition
            .filter { it.type == "function" } // TODO: What about events ?
            .forEach {
                val funSpec = if (it.inputs.isEmpty()) {
                    FunSpec.builder(it.name.decapitalize())
                        .returns(
                            SolidityUtils.getFunctionReturnType(it)
                        )
                        .addCode(
                            "return ${contractName.decapitalize()}.${it.name.decapitalize()}().send()"
                        )
                        .addModifiers(KModifier.OVERRIDE)
                        .build()
                } else {
                    val nameClass = ClassName(
                        "$packageName.core.${contractName.decapitalize()}.model",
                        "${it.name.capitalize()}Parameters"
                    )

                    FunSpec.builder(it.name.decapitalize())
                        .addParameter(
                            "${it.name.decapitalize()}Parameters",
                            nameClass
                        )
                        .returns(TransactionReceipt::class.asTypeName())
                        .addCode(
                            """
                                return ${contractName.decapitalize()}.${it.name.decapitalize()}(
                                    ${getCallParameters(it.inputs, it.name)}
                                ).send()
                            """.trimIndent()
                        )
                        .addModifiers(KModifier.OVERRIDE)
                        .build()
                }
                functions.add(funSpec)
            }
        return functions
    }

    private fun getCallParameters(inputs: MutableList<AbiDefinition.NamedType>, functionName: String): String {
        var callParameters = ""
        inputs.forEach {
            callParameters += "${functionName.decapitalize()}Parameters.${it.name.decapitalize()},"
        }
        return callParameters.removeSuffix(",")
    }
}
