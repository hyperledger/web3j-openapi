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
import org.junit.jupiter.api.Test
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.plusParameter
import com.squareup.kotlinpoet.asClassName
import org.web3j.protocol.core.methods.response.AbiDefinition
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class SolidityUtilsTest {

    @Test
    fun `getNativeArrayType for parameters Test `() {
        val expectedResult = ClassName("kotlin.collections", "MutableList")
            .plusParameter(
                ClassName("kotlin.collections", "MutableList")
                    .plusParameter(
                        Integer::class.asClassName()
                    )
            )
        val actualResult = SolidityUtils.getNativeType("int[10][20]", true)

        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `getNativeArrayType for returns Test `() {
        val expectedResult = ClassName("kotlin.collections", "MutableList")
            .plusParameter(
                ANY.copy(true)
            ).copy(true)
        val actualResult = SolidityUtils.getNativeType("int[10][20]", false)

        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `getFunctionReturnType for PrimitivesModel Test`() {
        val expectedResult = ClassName("org.web3j.openapi.core.models", "PrimitivesModel")
            .parameterizedBy(String::class.asClassName())

        val actualResult = SolidityUtils.getFunctionReturnType(
            AbiDefinition().apply {
                isConstant = true
                outputs = listOf(AbiDefinition.NamedType("param1", "address"))
            }
        )

        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun `getFunctionReturnType for TransactionReceiptModel Test`() {
        val expectedResult = ClassName("org.web3j.openapi.core.models", "TransactionReceiptModel")

        val actualResult = SolidityUtils.getFunctionReturnType(
            AbiDefinition().apply {
                isConstant = false
                outputs = listOf(AbiDefinition.NamedType("param1", "address"))
            }
        )

        assertThat(actualResult).isEqualTo(expectedResult)
    }

    @Test
    fun getMultipleFunctionReturnTypeTest() {
        val expectedResult = ClassName("org.web3j.tuples.generated", "Tuple2")
            .parameterizedBy(
                listOf(
                    Integer::class.asClassName(),
                    String::class.asClassName()
                ))

        val actualResult = SolidityUtils.getFunctionReturnType(
            AbiDefinition().apply {
                isConstant = true
                outputs = listOf(
                    AbiDefinition.NamedType("param1", "int"),
                    AbiDefinition.NamedType("param2", "address")
                )
            }
        )

        assertThat(actualResult).isEqualTo(expectedResult)
    }
}
