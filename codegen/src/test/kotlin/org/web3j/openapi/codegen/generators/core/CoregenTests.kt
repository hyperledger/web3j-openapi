package org.web3j.openapi.codegen.generators.core

import assertk.assertThat
import assertk.assertions.isSuccess
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.coregen.CoreGenerator
import org.web3j.openapi.codegen.coregen.subgenerators.CoreApiGenerator
import org.web3j.openapi.codegen.utils.GeneratorUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CoregenTests {

    @TempDir
    lateinit var tempFolder: File

    private val contractsFolder = Path.of(
        "src",
        "test",
        "resources",
        "contracts").toFile()

    private val contractsConfiguration = GeneratorUtils.loadContractConfigurations(
        listOf(contractsFolder), listOf(contractsFolder)
    )
    init {
        if (contractsConfiguration.isEmpty()) throw FileNotFoundException("No test contracts found!")
    }

    @Test
    @Order(1)
    fun `Core lifecycle and resources test`() {
        contractsConfiguration.forEach { contractConfiguration ->
            assertThat {
                CoreApiGenerator(
                    "com.test",
                    tempFolder.canonicalPath,
                    contractConfiguration.contractDetails
                ).generate()
            }.isSuccess()
        }
    }

    @Test
    @Order(2)
    fun `Core module test`() {
        assertThat {
            CoreGenerator(
                GeneratorConfiguration(
                    "testApp",
                    "com.test",
                    tempFolder.canonicalPath,
                    tempFolder,
                    contractsConfiguration,
                    20
                )
            ).generate()
        }.isSuccess()
    }
}