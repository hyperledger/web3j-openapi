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
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.GeneratorUtils.argumentName
import org.web3j.openapi.codegen.utils.getReturnType
import org.web3j.openapi.codegen.utils.getStructCallParameters
import org.web3j.openapi.codegen.utils.isTransactional
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File

internal class ResourcesImplGenerator(
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
            ).addSuperinterface(contractResourceClass)

        resourcesClass.addFunctions(generateFunctions())
        resourcesClass.addProperties(generateEvents())

        return resourcesFile
            .addType(resourcesClass.build())
            .build()
    }

    private fun generateEvents(): List<PropertySpec> {
        val events = mutableListOf<PropertySpec>()
        resourcesDefinition
            .filter { it.type == "event" }
            .forEach { abiDefinition ->
                val eventResourceImplClass = ClassName(
                    "$packageName.server.${contractName.toLowerCase()}",
                    "${abiDefinition.name.capitalize()}EventResourceImpl"
                )
                val propertySpec =
                    PropertySpec.builder("${abiDefinition.name.decapitalize()}Events", eventResourceImplClass)
                        .initializer("${abiDefinition.name.capitalize()}EventResourceImpl(${contractName.decapitalize()})")
                        .addModifiers(KModifier.OVERRIDE)
                events.add(propertySpec.build())
            }
        return events
    }

    private fun generateFunctions(): List<FunSpec> {
        val functions = mutableListOf<FunSpec>()
        resourcesDefinition
            .filter { it.type == "function" }
            .forEach {
                if (!it.isTransactional() && it.outputs.isEmpty()) return@forEach
                val returnType = it.getReturnType(packageName, contractName.toLowerCase())
                val funSpec = FunSpec.builder(it.name)
                    .returns(returnType)
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
                        ${contractName.decapitalize()}.${getFunctionName(it.name)}(
                                ${getCallParameters(it.inputs, it.name)}
                            ).send()
                    """.trimIndent()
                }

                funSpec.addCode(wrapCode(code, returnType.toString()))
                functions.add(funSpec.build())
            }
        return functions
    }

    private fun wrapCode(code: String, returnType: String): String {
        return if (returnType.startsWith("org.web3j.openapi.core.models.TransactionReceiptModel"))
            "return TransactionReceiptModel($code)"
        else if (returnType.startsWith("org.web3j.openapi.core.models.PrimitivesModel"))
            "return $returnType($code)"
        else if (returnType.startsWith("org.web3j.tuples")) {
            wrapTuplesCode(code, returnType)
        } else if (returnType.contains("StructModel"))
            "return $code.toModel()"
        else "return $code"
    }

    private fun wrapTuplesCode(code: String, returnType: String): String {
        val components = returnType.substringAfter("<")
            .removeSuffix(">")
            .split(",")

        val variableNames = components.mapIndexed { index, component ->
            if (component.endsWith("StructModel")) "${component.removeSuffix("StructModel").substringAfterLast(".").decapitalize()}$index"
            else "${component.substringBefore("<").substringAfterLast(".").decapitalize()}$index"
        }.joinToString(",")

        val tupleConstructor = components.mapIndexed { index, component ->
            if (component.endsWith("StructModel")) "${component.removeSuffix("StructModel").substringAfterLast(".").decapitalize()}$index.toModel()"
            else "${component.substringBefore("<").substringAfterLast(".").decapitalize()}$index"
        }.joinToString(",")

        return """val ($variableNames) = $code
                return Tuple${components.size}($tupleConstructor)
            """.trimMargin()
    }

    private fun getFunctionName(name: String): String {
        return if (name == "short" || name == "long" || name == "double" || name == "float" || name == "char") "_$name"
        else name
    }

    private fun getCallParameters(inputs: MutableList<AbiDefinition.NamedType>, functionName: String): String {
        var callParameters = ""
        inputs.forEachIndexed { index, input ->
            callParameters +=
                if (input.type == "tuple")
                    "${getStructCallParameters(contractName, input, functionName, "${functionName.decapitalize()}Parameters.${argumentName(input.name, index)}")},"
                else
                    "${functionName.decapitalize()}Parameters.${argumentName(input.name, index)},"
        }
        return callParameters.removeSuffix(",")
    }
}
