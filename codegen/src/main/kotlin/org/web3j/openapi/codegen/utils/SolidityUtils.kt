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
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.tuples.generated.Tuple4
import java.io.File
import java.math.BigInteger

object SolidityUtils {

    fun getNativeType(typeName: String, isParameter: Boolean = true): TypeName {
        // TODO: support for Fixed point numbers, enums, mappings, struct, library, multiple returns
        return if (typeName == "address") {
            String::class.asTypeName()
        } else if (typeName == "string") {
            String::class.asTypeName()
        } else if (typeName == "int") {
            Integer::class.asTypeName()
        } else if (typeName.endsWith("]")) {
            getNativeArrayType(typeName, isParameter)
        } else if (typeName.startsWith("uint") || typeName.startsWith("int")) {
            BigInteger::class.asTypeName()
        } else if (typeName == "byte") {
            Byte::class.asTypeName()
        } else if (typeName.startsWith("bytes") || typeName == "dynamicbytes") {
            ByteArray::class.asTypeName()
        } else if (typeName == "bool" || typeName == "boolean") {
            Boolean::class.asTypeName()
        } else if (typeName.toLowerCase() == "float") {
            Float::class.asTypeName()
        } else if (typeName.toLowerCase() == "double") {
            Double::class.asTypeName()
        } else if (typeName.toLowerCase() == "short") {
            Short::class.asTypeName()
        } else if (typeName.toLowerCase() == "long") {
            Long::class.asTypeName()
        } else if (typeName.toLowerCase() == "char") {
            Character::class.asTypeName()
        } else {
            throw UnsupportedOperationException(
                "Unsupported type: $typeName, no native type mapping exists."
            )
        }
    }

    private fun getNativeArrayType(typeName: String, isParameter: Boolean): TypeName {
        return if (isParameter) {
            ClassName("kotlin.collections", "MutableList")
                .plusParameter(
                    getNativeType(typeName.substringBeforeLast("["), isParameter)
                )
        } else {
            ClassName("kotlin.collections", "MutableList")
                .plusParameter(
                    ANY.copy(true)
                ).copy(true)
        }
    }

    fun getFunctionReturnType(it: AbiDefinition): TypeName {
        return if (isFunctionDefinitionConstant(it)) {
            if (it.outputs.size == 1) getNativeType(it.outputs.first().type, false)
            else getMultipleReturnType(it.outputs)
        } else ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel")
    }

    fun isFunctionDefinitionConstant(it: AbiDefinition): Boolean {
        val pureOrView = "pure" == it.stateMutability || "view" == it.stateMutability
        return it.isConstant || pureOrView
    }

    private fun getMultipleReturnType(outputs: List<AbiDefinition.NamedType>): TypeName {
        return ClassName("org.web3j.tuples.generated", "Tuple${outputs.size}")
            .parameterizedBy(
                outputs.map { output -> getNativeType(output.type) }
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
}
