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

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.utils.TemplateUtils.generateFromTemplate
import java.io.File

class TemplateUtilsTest {

    @TempDir
    lateinit var tempFolder: File

    @Test
    fun mustacheTemplateTest() {
        assertThat(
            TemplateUtils.mustacheTemplate(
                "server/src/GeneratedContractsResourceImpl.mustache"
            )).isNotNull()
    }

    @Test
    fun generateFromTemplateTest() {
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
