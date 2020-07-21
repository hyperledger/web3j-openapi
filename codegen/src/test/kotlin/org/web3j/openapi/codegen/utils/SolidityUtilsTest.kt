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

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.asClassName
import org.junit.jupiter.api.Test
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.protocol.core.methods.response.AbiDefinition.NamedType

class SolidityUtilsTest {

    @Test
    fun `toNativeArrayType for parameters`() {
        val expectedResult = ClassName("kotlin.collections", "List")
            .plusParameter(
                ClassName("kotlin.collections", "List")
                    .plusParameter(Integer::class.asClassName())
            )
        val actualResult = "int[10][20]".mapType()

        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `toNativeArrayType for returns`() {
        val expectedResult = ClassName("kotlin.collections", "List")
            .plusParameter(ANY.copy(true))
            .copy(true)

        val actualResult = "int[10][20]".mapType(false)

        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `getFunctionReturnType for PrimitivesModel`() {
        val expectedResult = ClassName("org.web3j.openapi.core.models", "PrimitivesModel")
            .parameterizedBy(String::class.asClassName())

        val actualResult = AbiDefinition().apply {
            outputs = listOf(NamedType("param1", "address"))
            isConstant = true
        }

        assertThat(actualResult.getReturnType()).isEqualTo(expectedResult)
    }

    @Test
    fun `getFunctionReturnType for TransactionReceiptModel`() {
        val expectedResult = ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel")

        val actualResult = AbiDefinition().apply {
            outputs = listOf(NamedType("param1", "address"))
            isConstant = false
        }

        assertThat(actualResult.getReturnType()).isEqualTo(expectedResult)
    }

    @Test
    fun getMultipleFunctionReturnTypeTest() {
        val expectedResult = ClassName("org.web3j.tuples.generated", "Tuple2")
            .parameterizedBy(
                listOf(
                    Integer::class.asClassName(),
                    String::class.asClassName()
                )
            )

        val actualResult = AbiDefinition().apply {
            outputs = listOf(
                NamedType("param1", "int"),
                NamedType("param2", "address")
            )
            isConstant = true
        }

        assertThat(actualResult.getReturnType()).isEqualTo(expectedResult)
    }
}
