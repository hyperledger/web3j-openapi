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
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File
import java.math.BigInteger
import java.util.ArrayList
import java.util.Comparator
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.stream.Collectors

/**
 * MapType maps solidity types to Java/Kotlin types.
 *
 * @param isParameter: refers if the type is a function/constructor/event parameter or a return type
 * @param structName: should be used in the case we are dealing with structs, if not, leave empty
 * @param packageName: should be used in the case of a struct, and it's used to refer the struct by its full struct name
 * @param contractName: same as package name: <code>packageName.contractName.structName(...)</code>
 */
internal fun String.mapType(isParameter: Boolean = true, structName: String = "", packageName: String = "", contractName: String = ""): TypeName {
    return if (this == "address" || this == "string") {
        getParameterMapping(isParameter, String::class.asTypeName())
    } else if (this == "int") {
        getParameterMapping(isParameter, Integer::class.asTypeName())
    } else if (endsWith("]")) {
        getParameterMapping(isParameter, toNativeArrayType(isParameter))
    } else if (startsWith("uint") || startsWith("int")) {
        getNumbersMapping(isParameter, this)
    } else if (this == "byte") {
        getParameterMapping(isParameter, Byte::class.asTypeName())
    } else if (startsWith("bytes") || this == "dynamicbytes") {
        getParameterMapping(isParameter, ByteArray::class.asTypeName())
    } else if (this == "bool" || this == "boolean") {
        getParameterMapping(isParameter, Boolean::class.asTypeName())
    } else if (toLowerCase() == "float") {
        getParameterMapping(isParameter, Float::class.asTypeName())
    } else if (toLowerCase() == "double") {
        getParameterMapping(isParameter, Double::class.asTypeName())
    } else if (toLowerCase() == "short") {
        getParameterMapping(isParameter, Short::class.asTypeName())
    } else if (toLowerCase() == "long") {
        getParameterMapping(isParameter, Long::class.asTypeName())
    } else if (toLowerCase() == "char") {
        getParameterMapping(isParameter, Character::class.asTypeName())
    } else if (toLowerCase() == "tuple") {
        getParameterMapping(isParameter, ClassName("$packageName.core.$contractName.model", "${structName}StructModel"))
    } else {
        throw UnsupportedOperationException(
            "Unsupported type: $this, no native type mapping exists."
        )
    }
}

/**
 * Maps the different number variations to their specific types depending on their size
 */
fun getNumbersMapping(isParameter: Boolean, type: String): TypeName {
    return if (isParameter) getParameterMapping(isParameter, BigInteger::class.asTypeName())
    else if (type == "uint8") getParameterMapping(isParameter, BigInteger::class.asTypeName()) // FIXME: remove this when web3j-codegen fixes this problem
    else if (type.startsWith("int") && type.substringAfter("int").toInt() < 16 ||
            type.startsWith("uint") && type.substringAfter("int").toInt() < 16)
        getParameterMapping(isParameter, Short::class.asTypeName())
    else if (type.startsWith("int") && type.substringAfter("int").toInt() <= 32 ||
            type.startsWith("uint") && type.substringAfter("int").toInt() < 32)
        getParameterMapping(isParameter, Integer::class.asTypeName())
    else if (type.startsWith("int") && type.substringAfter("int").toInt() <= 64 ||
            type.startsWith("uint") && type.substringAfter("int").toInt() < 64)
        getParameterMapping(isParameter, Long::class.asTypeName())
    else
        getParameterMapping(isParameter, BigInteger::class.asTypeName())
}

/**
 * Maps events indexed types using java native types
 */
fun String.mapIndexedType(): TypeName {
    return if (this == "string" || this.contains("[")) ByteArray::class.asTypeName()
    else mapType(true)
}

private fun getParameterMapping(isParameter: Boolean, typeName: TypeName): TypeName {
    return if (isParameter) typeName
    else {
        ClassName("org.web3j.openapi.core.models", "ResultModel")
            .parameterizedBy(typeName)
    }
}

private fun String.toNativeArrayType(isParameter: Boolean, structName: String = "", packageName: String = "", contractName: String = ""): TypeName {
    return if (isParameter) {
        ClassName("kotlin.collections", "List")
            .plusParameter(substringBeforeLast("[").mapType(isParameter, structName, packageName, contractName))
    } else {
        ClassName("kotlin.collections", "List")
            .plusParameter(ANY.copy(true)).copy(true)
    }
}

internal fun AbiDefinition.getReturnType(packageName: String = "", contractName: String = ""): TypeName {
    return if (!isTransactional()) {
        if (outputs.size == 1) {
            outputs.first().type.mapType(false, outputs.first().internalType.structName, packageName, contractName)
        } else {
            getParameterMapping(
                false,
                ClassName("org.web3j.tuples.generated", "Tuple${outputs.size}")
                    .parameterizedBy(outputs.map { it.type.mapType(true, it.internalType.structName, packageName, contractName).copy() }))
        }
    } else {
        ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel")
    }
}

internal fun AbiDefinition.isTransactional(): Boolean {
    return !(isConstant || "pure" == stateMutability || "view" == stateMutability)
}

// FIXME: use web3j-codegen one
internal fun loadContractDefinition(absFile: File?): List<AbiDefinition> {
    val objectMapper: ObjectMapper =
        org.web3j.protocol.ObjectMapperFactory.getObjectMapper()
    val abiDefinition: Array<AbiDefinition> =
        objectMapper.readValue(
            absFile,
            Array<AbiDefinition>::class.java
        )
    return listOf(*abiDefinition)
}

internal val String.structName
    get() = split(".").last()

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
    val structName = input.internalType.structName
    val decapitalizedFunctionName = functionName.decapitalize() // FIXME: do we need this ?
    val parameters = input.components.joinToString(",") { component ->
        if (component.components.isNullOrEmpty()) "$callTree.${component.name}"
        else getStructCallParameters(contractName, component, decapitalizedFunctionName, "$callTree.${component.name}".removeSuffix("."))
    }
    return "$contractName.$structName($parameters)"
}
