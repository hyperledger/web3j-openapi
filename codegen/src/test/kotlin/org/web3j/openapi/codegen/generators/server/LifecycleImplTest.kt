package org.web3j.openapi.codegen.generators.server

import assertk.assertThat
import assertk.assertions.isSuccess
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.coregen.subgenerators.CoreFunctionsModelGenerator
import org.web3j.openapi.codegen.servergen.subgenerators.LifecycleImplGenerator
import org.web3j.openapi.codegen.utils.GeneratorUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

class LifecycleImplTest {

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
    fun `Server LifecycleImpl tests`() {
        contractsConfiguration.forEach { contractConfiguration ->
            assertThat {
                LifecycleImplGenerator(
                    "com.test",
                    tempFolder.canonicalPath,
                    contractConfiguration.contractDetails
                ).generate()
            }.isSuccess()
        }
    }
}