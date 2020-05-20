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
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Utf8String
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File
import java.math.BigInteger

object SolidityUtils {

    fun getNativeType(typeName: String, param: Boolean = true): TypeName {
        // TODO: support for Fixed point numbers, enums, mappings, struct, library, multiple returns
        return if (typeName == "address") {
            String::class.asTypeName()
        } else if (typeName == "string" || typeName == "") {
            String::class.asTypeName()
        } else if (typeName.endsWith("]")) {
            getNativeArrayType(typeName, param) // TODO
        } else if (
            typeName.startsWith("uint")
            || typeName.startsWith("int")
            || typeName == "float"
            || typeName == "double"
            || typeName == "short"
            || typeName == "long"
        ) {
            BigInteger::class.asTypeName()
        } else if (typeName =="byte") {
            Byte::class.asTypeName()
        } else if (typeName.startsWith("bytes") || typeName == "dynamicbytes") {
            ByteArray::class.asTypeName()
        } else if (typeName == "bool") {
            Boolean::class.asTypeName()
            // boolean cannot be a parameterized type
        } else {
            throw UnsupportedOperationException(
                "Unsupported type: $typeName, no native type mapping exists."
            )
        }
    }

    private fun getNativeArrayType(typeName: String, param: Boolean): TypeName {
        return if (param) {
            ClassName("kotlin.collections", "MutableList")
                .plusParameter(
                    getNativeType(typeName.substringBeforeLast("["), param)
                )
        } else {
            ClassName("kotlin.collections", "MutableList")
                .plusParameter(
                    ANY.copy(true)
                ).copy(true)
        }
    }

    fun getFunctionReturnType(it: AbiDefinition): TypeName {
        val pureOrView = "pure" == it.stateMutability || "view" == it.stateMutability
        val isFunctionDefinitionConstant = it.isConstant || pureOrView

        return if (isFunctionDefinitionConstant)
            getNativeType(it.outputs.first().type, false)
        else ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel")
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
