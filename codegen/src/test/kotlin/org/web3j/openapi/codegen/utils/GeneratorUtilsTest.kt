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

import org.junit.jupiter.api.Test
import org.web3j.openapi.codegen.utils.GeneratorUtils.handleDuplicateInputNames
import org.web3j.openapi.codegen.utils.GeneratorUtils.handleDuplicateNames
import java.io.File
import java.nio.file.Paths

class GeneratorUtilsTest {

    private val contractsFolder = Paths.get(
        "src",
        "test",
        "resources",
        "contracts").toFile()

    @Test
    fun `Function names duplicates handling test`() {
        val duplicatesAbi = File(
            Paths.get(
                contractsFolder.absolutePath,
                "duplicate",
                "build",
                "DuplicateField.abi"
            ).toString()
        )

        val contractAbiDefinition = loadContractDefinition(duplicatesAbi)
        val sanitizedAbiDefinitions = handleDuplicateNames(contractAbiDefinition, "function")

        assert(sanitizedAbiDefinitions.filter { it.type == "function" }.map { it.name.decapitalize() }.toSet().size
            == loadContractDefinition(duplicatesAbi).filter { it.type == "function" }.size)
    }

    @Test
    fun `Inputs duplicates handling test`() {
        val duplicatesAbi = File(
            Paths.get(
                contractsFolder.absolutePath,
                "duplicate",
                "build",
                "DuplicateField.abi"
            ).toString()
        )

        val inputWithDuplicates = loadContractDefinition(duplicatesAbi).filter { it.name == "Sum" }.map { it.inputs }.first()
        val sanitizedInputs = handleDuplicateInputNames(inputWithDuplicates)

        assert(sanitizedInputs.map { it.name.decapitalize() }.toSet().size
                == sanitizedInputs.size)
    }
}
