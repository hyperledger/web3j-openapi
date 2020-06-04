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
package org.web3j.openapi.codegen.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import org.web3j.abi.datatypes.StructType
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File
import java.math.BigInteger
import java.util.*
import java.util.stream.Collectors

object SolidityUtils {

    fun getNativeType(typeName: String, isParameter: Boolean = true, structName: String = "", packageName: String = "", contractName: String = ""): TypeName {
        // TODO: support for enums, struct
        val primitivesModel = ClassName("org.web3j.openapi.core.models", "PrimitivesModel")
        return if (typeName == "address") {
            if (isParameter) String::class.asTypeName()
            else primitivesModel.parameterizedBy(String::class.asClassName())
        } else if (typeName == "string") {
            if (isParameter) String::class.asTypeName()
            else primitivesModel.parameterizedBy(String::class.asClassName())
        } else if (typeName == "int") {
            if (isParameter) Integer::class.asTypeName()
            else primitivesModel.parameterizedBy(Integer::class.asClassName())
        } else if (typeName.endsWith("]")) {
            getNativeArrayType(typeName, isParameter, structName, packageName, contractName)
        } else if (typeName.startsWith("uint") || typeName.startsWith("int")) {
            if (isParameter) BigInteger::class.asTypeName()
            else primitivesModel.parameterizedBy(BigInteger::class.asClassName())
        } else if (typeName == "byte") {
            if (isParameter) Byte::class.asTypeName()
            else primitivesModel.parameterizedBy(Byte::class.asClassName())
        } else if (typeName.startsWith("bytes") || typeName == "dynamicbytes") {
            if (isParameter) ByteArray::class.asTypeName()
            else primitivesModel.parameterizedBy(ByteArray::class.asClassName())
        } else if (typeName == "bool" || typeName == "boolean") {
            if (isParameter) Boolean::class.asTypeName()
            else primitivesModel.parameterizedBy(Boolean::class.asClassName())
        } else if (typeName.toLowerCase() == "float") {
            if (isParameter) Float::class.asTypeName()
            else primitivesModel.parameterizedBy(Float::class.asClassName())
        } else if (typeName.toLowerCase() == "double") {
            if (isParameter) Double::class.asTypeName()
            else primitivesModel.parameterizedBy(Double::class.asClassName())
        } else if (typeName.toLowerCase() == "short") {
            if (isParameter) Short::class.asTypeName()
            else primitivesModel.parameterizedBy(Short::class.asClassName())
        } else if (typeName.toLowerCase() == "long") {
            if (isParameter) Long::class.asTypeName()
            else primitivesModel.parameterizedBy(Long::class.asClassName())
        } else if (typeName.toLowerCase() == "char") {
            if (isParameter) Character::class.asTypeName()
            else primitivesModel.parameterizedBy(Character::class.asClassName())
        } else if (typeName.toLowerCase() == "tuple") {
            ClassName("${packageName}.core.${contractName}.model", "${structName}StructModel")
        } else {
            throw UnsupportedOperationException(
                "Unsupported type: $typeName, no native type mapping exists."
            )
        }
    }

    private fun getNativeArrayType(
        typeName: String,
        isParameter: Boolean,
        structName: String = "",
        packageName: String = "",
        contractName: String = ""
    ): TypeName {
        return if (isParameter) {
            ClassName("kotlin.collections", "MutableList")
                .plusParameter(
                    getNativeType(typeName.substringBeforeLast("["), isParameter, structName, packageName, contractName)
                )
        } else {
            ClassName("kotlin.collections", "MutableList")
                .plusParameter(
                    ANY.copy(true)
                ).copy(true)
        }
    }

    fun getFunctionReturnType(it: AbiDefinition, packageName: String = "", contractName: String = ""): TypeName {
        return if (isFunctionDefinitionConstant(it)) {
            if (it.outputs.size == 1) getNativeType(it.outputs.first().type, false, getStructName(it.outputs.first().internalType), packageName, contractName)
            else getMultipleReturnType(it.outputs, packageName, contractName)
        } else ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel")
    }

    fun isFunctionDefinitionConstant(it: AbiDefinition): Boolean {
        val pureOrView = "pure" == it.stateMutability || "view" == it.stateMutability
        return it.isConstant || pureOrView
    }

    private fun getMultipleReturnType(
        outputs: List<AbiDefinition.NamedType>,
        packageName: String = "",
        contractName: String = ""
    ): TypeName {
        return ClassName("org.web3j.tuples.generated", "Tuple${outputs.size}")
            .parameterizedBy(
                outputs.map { output -> getNativeType(output.type, true, getStructName(output.internalType), packageName, contractName).copy()  }
            )
    }

    fun loadContractDefinition(absFile: File?): List<AbiDefinition> { // TODO: use web3j-codegen one
        val objectMapper: ObjectMapper =
            org.web3j.protocol.ObjectMapperFactory.getObjectMapper()
        val abiDefinition: Array<AbiDefinition> =
            objectMapper.readValue(
                absFile,
                Array<AbiDefinition>::class.java
            )
        return listOf(*abiDefinition)
    }

    fun getStructName(internalType: String) = internalType.split(".").last() // FIXME: is this correct ?

    // FIXME: Use web3j.codegen one
    fun extractStructs(
        functionDefinitions: List<AbiDefinition>
    ): List<AbiDefinition.NamedType?>? {
        val structMap: HashMap<Int, AbiDefinition.NamedType> =
            LinkedHashMap()
        functionDefinitions.stream()
            .flatMap { definition: AbiDefinition ->
                val parameters: MutableList<AbiDefinition.NamedType> =
                    ArrayList()
                parameters.addAll(definition.inputs)
                parameters.addAll(definition.outputs)
                parameters.stream()
                    .filter { namedType: AbiDefinition.NamedType -> namedType.type == "tuple" }
            }
            .forEach { namedType: AbiDefinition.NamedType ->
                structMap[namedType.structIdentifier()] = namedType
                extractNested(namedType)!!.stream()
                    .filter { nestedNamedStruct -> nestedNamedStruct!!.type == "tuple" }
                    .forEach { nestedNamedType ->
                        structMap[nestedNamedType!!.structIdentifier()] = nestedNamedType
                    }
            }
        return structMap.values.stream()
            .sorted(Comparator.comparingInt(AbiDefinition.NamedType::nestedness))
            .collect(Collectors.toList())
    }

    // FIXME: Use web3j.codegen one
    private fun extractNested(
        namedType: AbiDefinition.NamedType
    ): Collection<AbiDefinition.NamedType?>? {
        return if (namedType.components.size == 0) {
            ArrayList()
        } else {
            val nestedStructs: MutableList<AbiDefinition.NamedType?> =
                ArrayList()
            namedType
                .components
                .forEach { nestedNamedStruct ->
                    nestedStructs.add(nestedNamedStruct)
                    nestedStructs.addAll(extractNested(nestedNamedStruct)!!)
                }
            nestedStructs
        }
    }

    fun getStructCallParameters(contractName: String, input: AbiDefinition.NamedType, functionName: String, callTree: String = ""): String {
        val structName = getStructName(input.internalType)
        val decapitalizedFunctionName = functionName.decapitalize() // FIXME: do we need this ?
        val parameters = input.components.joinToString(",") { component ->
            if (component.components.isNullOrEmpty()) "$callTree.${component.name}"
            else getStructCallParameters(contractName, component, decapitalizedFunctionName, "${callTree}.${component.name}".removeSuffix("."))
        }
        return "$contractName.$structName($parameters)"
    }
}
