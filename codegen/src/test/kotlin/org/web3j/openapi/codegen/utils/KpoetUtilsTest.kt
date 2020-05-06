package org.web3j.openapi.codegen.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.web3j.openapi.codegen.Folders
import org.web3j.openapi.codegen.utils.KPoetUtils.inputsToDataClass
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.protocol.core.methods.response.AbiDefinition.*
import java.io.File
import java.nio.file.Path

class KpoetUtilsTest {

    val tempFolder = Folders.tempBuildFolder()

    @Test
    fun inputsToDataClassTest() {
        val expectedOutput = """// Copyright 2020 Web3 Labs Ltd.
            //
            //  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
            //  the License. You may obtain a copy of the License at
            //
            //  http://www.apache.org/licenses/LICENSE-2.0
            //
            //  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
            //  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
            //  specific language governing permissions and limitations under the License.
            package test
            
            import java.math.BigInteger
            import kotlin.String
            
            data class TestFunctionParameters(
              val number: BigInteger,
              val string: String
            )
            """.replace("\\s".toRegex(), "")

        val namedTypes = listOf(
            NamedType("number", "uint256"),
            NamedType("string", "string")
        )
        inputsToDataClass(
            "test",
            "testFunction",
            namedTypes,
            "Parameters"
        ).writeTo(tempFolder)

        val actualOutput = File(
            Path.of(
                tempFolder.absolutePath,
                "test",
                "TestFunctionParameters.kt"
            ).toString()
        ).readText().replace("\\s".toRegex(), "")

        assertThat(actualOutput).isEqualTo(expectedOutput)
    }
}