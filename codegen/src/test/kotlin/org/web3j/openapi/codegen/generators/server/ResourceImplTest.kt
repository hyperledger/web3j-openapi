package org.web3j.openapi.codegen.generators.server

import assertk.assertThat
import assertk.assertions.isSuccess
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.servergen.subgenerators.LifecycleImplGenerator
import org.web3j.openapi.codegen.servergen.subgenerators.ResourcesImplGenerator
import org.web3j.openapi.codegen.utils.GeneratorUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

class ResourceImplTest {

    @TempDir
    lateinit var tempFolder: File

    private val contractsFolder = Path.of(
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
    fun `Server ResourceImpl tests`() {
        contractsConfiguration.forEach { contractConfiguration ->
            assertThat {
                ResourcesImplGenerator(
                    "com.test",
                    contractConfiguration.contractDetails.capitalizedContractName,
                    contractConfiguration.contractDetails.abiDefinitions,
                    tempFolder.canonicalPath
                ).generate()
            }.isSuccess()
        }
    }
}