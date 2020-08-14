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
package org.web3j.openapi.codegen.generators.core

import assertk.assertThat
import assertk.assertions.isSuccess
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.coregen.subgenerators.CoreDeployModelGenerator
import org.web3j.openapi.codegen.coregen.subgenerators.CoreEventsModelGenerator
import org.web3j.openapi.codegen.coregen.subgenerators.CoreFunctionsModelGenerator
import org.web3j.openapi.codegen.coregen.subgenerators.CoreStructsModelGenerator
import org.web3j.openapi.codegen.utils.GeneratorUtils
import org.web3j.openapi.codegen.utils.GeneratorUtils.sanitizedName
import org.web3j.openapi.codegen.utils.extractStructs
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

class ModelsTests {

    @TempDir
    lateinit var tempFolder: File

    private val contractsFolder = Paths.get(
        "src",
        "test",
        "resources",
        "contracts"
    ).toFile()

    private val contractsConfiguration = GeneratorUtils.loadContractConfigurations(
        listOf(contractsFolder), listOf(contractsFolder)
    )

    init {
        if (contractsConfiguration.isEmpty()) throw FileNotFoundException("No test contracts found!")
    }

    @Test
    fun `Core deploy models test`() {
        contractsConfiguration.forEach { contractConfiguration ->
            assertThat {
                contractConfiguration
                    .contractDetails
                    .abiDefinitions.firstOrNull { it.type == "constructor" && !it.inputs.isNullOrEmpty() }
                    ?.inputs
                    ?.let {
                        CoreDeployModelGenerator(
                            "com.test",
                            contractConfiguration.contractDetails.capitalizedContractName,
                            tempFolder.canonicalPath,
                            it
                        ).generate()
                    }
            }.isSuccess()
        }
    }

    @Test
    fun `Core function models test`() {
        contractsConfiguration.forEach { contractConfiguration ->
            assertThat {
                contractConfiguration
                    .contractDetails
                    .abiDefinitions.firstOrNull { it.type == "function" && !it.inputs.isNullOrEmpty() }
                    ?.let {
                        CoreFunctionsModelGenerator(
                            "com.test",
                            contractConfiguration.contractDetails.capitalizedContractName,
                            it.sanitizedName(),
                            tempFolder.canonicalPath,
                            it.inputs
                        ).generate()
                    }
            }.isSuccess()
        }
    }

    @Test
    fun `Core events models test`() {
        contractsConfiguration.forEach { contractConfiguration ->
            assertThat {
                contractConfiguration
                    .contractDetails
                    .abiDefinitions.firstOrNull { it.type == "event" }
                    ?.let {
                        CoreEventsModelGenerator(
                            "com.test",
                            contractConfiguration.contractDetails.capitalizedContractName,
                            it.sanitizedName(),
                            tempFolder.canonicalPath,
                            it.inputs
                        ).generate()
                    }
            }.isSuccess()
        }
    }

    @Test
    fun `Core structs models test`() {
        contractsConfiguration.forEach { contractConfiguration ->
            assertThat {
                extractStructs(contractConfiguration.contractDetails.abiDefinitions)?.forEach { structDefinition ->
                    CoreStructsModelGenerator(
                        packageName = "com.test",
                        contractName = contractConfiguration.contractDetails.capitalizedContractName,
                        functionName = structDefinition!!.internalType.split(".").last(),
                        folderPath = tempFolder.canonicalPath,
                        components = structDefinition.components
                    ).generate()
                }
            }.isSuccess()
        }
    }
}
