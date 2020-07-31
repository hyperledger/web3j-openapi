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
package org.web3j.openapi.codegen.generators.server

import assertk.assertThat
import assertk.assertions.isSuccess
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.servergen.subgenerators.StructExtensionsGenerator
import org.web3j.openapi.codegen.utils.GeneratorUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

class StructExtensionsTest {

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
    fun `Server structs extensions test`() {
        contractsConfiguration.forEach { contractConfiguration ->
            assertThat {
                StructExtensionsGenerator(
                    packageName = "com.test",
                    contractName = contractConfiguration.contractDetails.capitalizedContractName,
                    folderPath = tempFolder.canonicalPath,
                    resourcesDefinition = contractConfiguration.contractDetails.abiDefinitions
                ).generate()
            }.isSuccess()
        }
    }
}
