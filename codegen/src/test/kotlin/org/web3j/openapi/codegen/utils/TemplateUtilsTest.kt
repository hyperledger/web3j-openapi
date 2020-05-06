package org.web3j.openapi.codegen.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.web3j.openapi.codegen.Folders
import org.web3j.openapi.codegen.utils.TemplateUtils.generateFromTemplate

class TemplateUtilsTest {
    val tempFolder = Folders.tempBuildFolder()
    @Test
    fun mustacheTemplateTest(){
        assertThat(
            TemplateUtils.mustacheTemplate(
                "server/src/GeneratedContractsResourceImpl.mustache"
            )).isNotNull()
    }

    @Test
    fun generateFromTemplateTest(){
        val expectedOutput = "test"

        val actualOutput = generateFromTemplate(
            mapOf("test" to "test"),
            tempFolder.absolutePath,
            "testTemplate.txt",
            TemplateUtils.mustacheTemplate(
                "testTemplate.mustache"
            )
        ).readText().replace("\\s".toRegex(), "")

        assertThat(actualOutput).isEqualTo(expectedOutput)
    }
}