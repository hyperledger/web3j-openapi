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

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.ruleset.experimental.ExperimentalRuleSetProvider
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import mu.KLogging
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

internal object CopyUtils : KLogging() {

    private val ruleSets = listOf(
        StandardRuleSetProvider().get(),
        ExperimentalRuleSetProvider().get()
    )

    fun copyResource(name: String, outputDir: File) {
        Files.copy(
            javaClass.classLoader.getResourceAsStream(name)!!,
            outputDir.resolve(name).toPath(),
            StandardCopyOption.REPLACE_EXISTING
        )
    }

    fun createTree(outputDir: String, packageDir: String, module: String = ""): String {
        val folder = File(
            Paths.get(
                outputDir,
                packageDir,
                module
            ).toString()
        ).apply { mkdirs() }
        return folder.absolutePath
    }

    /**
     * Format a given Kotlin file using KtLint.
     */
    fun kotlinFormat(file: File) {
        KtLint.format(
            KtLint.Params(
                ruleSets = ruleSets,
                cb = { _, _ -> },
                text = file.readText()
            )
        ).run {
            file.writeText(this)
        }
    }
}
