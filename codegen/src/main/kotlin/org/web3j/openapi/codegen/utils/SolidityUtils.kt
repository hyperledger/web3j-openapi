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
import kotlin.reflect.KClass

internal fun String.toNativeType(isParameter: Boolean = true): TypeName {
    // TODO: support for Fixed point numbers, enums, mappings, struct, library
    return if (this == "address" || this == "string") {
        getParameterMapping(isParameter, String::class)
    } else if (this == "int") {
        getParameterMapping(isParameter, Integer::class)
    } else if (endsWith("]")) {
        toNativeArrayType(isParameter)
    } else if (startsWith("uint") || startsWith("int")) {
        getParameterMapping(isParameter, BigInteger::class)
    } else if (this == "byte") {
        getParameterMapping(isParameter, Byte::class)
    } else if (startsWith("bytes") || this == "dynamicbytes") {
        getParameterMapping(isParameter, ByteArray::class)
    } else if (this == "bool" || this == "boolean") {
        getParameterMapping(isParameter, Boolean::class)
    } else if (toLowerCase() == "float") {
        getParameterMapping(isParameter, Float::class)
    } else if (toLowerCase() == "double") {
        getParameterMapping(isParameter, Double::class)
    } else if (toLowerCase() == "short") {
        getParameterMapping(isParameter, Short::class)
    } else if (toLowerCase() == "long") {
        getParameterMapping(isParameter, Long::class)
    } else if (toLowerCase() == "char") {
        getParameterMapping(isParameter, Character::class)
    } else {
        throw UnsupportedOperationException(
            "Unsupported type: ${this}, no native type mapping exists."
        )
    }
}

private fun getParameterMapping(isParameter: Boolean, kClass: KClass<*>): TypeName {
    return if (isParameter) kClass.asTypeName()
    else {
        ClassName("org.web3j.openapi.core.models", "PrimitivesModel")
            .parameterizedBy(kClass.asClassName())
    }
}

private fun String.toNativeArrayType(isParameter: Boolean): TypeName {
    return if (isParameter) {
        ClassName("kotlin.collections", "List")
            .plusParameter(substringBeforeLast("[").toNativeType(isParameter))
    } else {
        ClassName("kotlin.collections", "List")
            .plusParameter(ANY.copy(true)).copy(true)
    }
}

internal val AbiDefinition.returnType: TypeName
    get() = if (!isTransactional()) {
        if (outputs.size == 1) {
            outputs.first().type.toNativeType(false)
        } else {
            ClassName("org.web3j.tuples.generated", "Tuple${outputs.size}")
                .parameterizedBy(outputs.map { it.type.toNativeType() })
        }
    } else {
        ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel")
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
