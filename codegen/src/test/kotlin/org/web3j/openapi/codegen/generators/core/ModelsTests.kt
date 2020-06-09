package org.web3j.openapi.codegen.generators.core

import assertk.assertThat
import assertk.assertions.isSuccess
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.coregen.subgenerators.CoreDeployModelGenerator
import org.web3j.openapi.codegen.coregen.subgenerators.CoreEventsModelGenerator
import org.web3j.openapi.codegen.coregen.subgenerators.CoreFunctionsModelGenerator
import org.web3j.openapi.codegen.utils.GeneratorUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

class ModelsTests {

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
                            it.name,
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
                    .abiDefinitions.firstOrNull { it.type == "event"}
                    ?.let {
                        CoreEventsModelGenerator(
                            "com.test",
                            contractConfiguration.contractDetails.capitalizedContractName,
                            it.name,
                            tempFolder.canonicalPath,
                            it.inputs
                        ).generate()
                    }
            }.isSuccess()
        }
    }
}