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
package org.web3j.openapi.codegen.generators

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.OpenApiGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.GeneratorUtils.loadContractConfigurations
import java.io.File
import java.nio.file.Paths

class GenerationTest {

    @TempDir
    lateinit var tempFolder: File

    @Test
    fun `Generated project gradle tasks test`() {
        val contractsFolder = Paths.get(
            "src",
            "test",
            "resources",
            "contracts").toFile()

        val generatorConfiguration = GeneratorConfiguration(
            projectName = "testProject",
            packageName = "com.test",
            outputDir = tempFolder.canonicalPath,
            contracts = loadContractConfigurations(
                listOf(contractsFolder), listOf(contractsFolder)
            ),
            contextPath = "test",
            withSwaggerUi = true
        )

        assertDoesNotThrow {
            OpenApiGenerator(generatorConfiguration).run {
                generate()
                generateSwaggerUI()
            }
        }
    }
}
