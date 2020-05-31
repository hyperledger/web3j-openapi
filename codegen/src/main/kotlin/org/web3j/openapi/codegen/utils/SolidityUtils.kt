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
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File
import java.math.BigInteger

internal fun String.toNativeType(isParameter: Boolean = true): TypeName {
    // TODO: support for Fixed point numbers, enums, mappings, struct, library
    val primitivesModel = ClassName("org.web3j.openapi.core.models", "PrimitivesModel")
    return if (this == "address") {
        if (isParameter) String::class.asTypeName()
        else primitivesModel.parameterizedBy(String::class.asClassName())
    } else if (this == "string") {
        if (isParameter) String::class.asTypeName()
        else primitivesModel.parameterizedBy(String::class.asClassName())
    } else if (this == "int") {
        if (isParameter) Integer::class.asTypeName()
        else primitivesModel.parameterizedBy(Integer::class.asClassName())
    } else if (endsWith("]")) {
        toNativeArrayType(isParameter)
    } else if (startsWith("uint") || startsWith("int")) {
        if (isParameter) BigInteger::class.asTypeName()
        else primitivesModel.parameterizedBy(BigInteger::class.asClassName())
    } else if (this == "byte") {
        if (isParameter) Byte::class.asTypeName()
        else primitivesModel.parameterizedBy(Byte::class.asClassName())
    } else if (startsWith("bytes") || this == "dynamicbytes") {
        if (isParameter) ByteArray::class.asTypeName()
        else primitivesModel.parameterizedBy(ByteArray::class.asClassName())
    } else if (this == "bool" || this == "boolean") {
        if (isParameter) Boolean::class.asTypeName()
        else primitivesModel.parameterizedBy(Boolean::class.asClassName())
    } else if (toLowerCase() == "float") {
        if (isParameter) Float::class.asTypeName()
        else primitivesModel.parameterizedBy(Float::class.asClassName())
    } else if (toLowerCase() == "double") {
        if (isParameter) Double::class.asTypeName()
        else primitivesModel.parameterizedBy(Double::class.asClassName())
    } else if (toLowerCase() == "short") {
        if (isParameter) Short::class.asTypeName()
        else primitivesModel.parameterizedBy(Short::class.asClassName())
    } else if (toLowerCase() == "long") {
        if (isParameter) Long::class.asTypeName()
        else primitivesModel.parameterizedBy(Long::class.asClassName())
    } else if (toLowerCase() == "char") {
        if (isParameter) Character::class.asTypeName()
        else primitivesModel.parameterizedBy(Character::class.asClassName())
    } else {
        throw IllegalArgumentException(
            "Unsupported type: $this, no native type mapping exists."
        )
    }
}

private fun String.toNativeArrayType(isParameter: Boolean): TypeName {
    return if (isParameter) {
        ClassName("kotlin.collections", "MutableList")
            .plusParameter(
                substringBeforeLast("[").toNativeType(isParameter)
            )
    } else {
        ClassName("kotlin.collections", "MutableList")
            .plusParameter(
                ANY.copy(true)
            ).copy(true)
    }
}

internal val AbiDefinition.returnType: TypeName
    get() = if (constant) {
        if (outputs.size == 1) {
            outputs.first().type.toNativeType(false)
        } else {
            ClassName("org.web3j.tuples.generated", "Tuple${outputs.size}")
                .parameterizedBy(outputs.map { it.type.toNativeType() })
        }
    } else {
        ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel")
    }

internal val AbiDefinition.constant: Boolean
    get() {
        val pureOrView = "pure" == stateMutability || "view" == stateMutability
        return isConstant || pureOrView
    }

// FIXME: use web3j-codegen one
fun loadContractDefinition(absFile: File?): List<AbiDefinition> {
    val objectMapper: ObjectMapper =
        org.web3j.protocol.ObjectMapperFactory.getObjectMapper()
    val abiDefinition: Array<AbiDefinition> =
        objectMapper.readValue(
            absFile,
            Array<AbiDefinition>::class.java
        )
    return listOf(*abiDefinition)
}
