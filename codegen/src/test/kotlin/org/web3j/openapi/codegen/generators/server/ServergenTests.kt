package org.web3j.openapi.codegen.generators.server

import assertk.assertThat
import assertk.assertions.isSuccess
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.servergen.ServerGenerator
import org.web3j.openapi.codegen.servergen.subgenerators.ResourcesImplGenerator
import org.web3j.openapi.codegen.utils.GeneratorUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

class ServergenTests {

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
        assertThat {
            ServerGenerator(
                GeneratorConfiguration(
                    "testApp",
                    "com.test",
                    tempFolder.canonicalPath,
                    contractsConfiguration,
                    20,
                     "0.1.0-SNAPSHOT"
                )
            ).generate()
        }.isSuccess()
    }
}