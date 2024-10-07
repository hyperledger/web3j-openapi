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

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter

internal object TemplateUtils {
    fun mustacheTemplate(filePath: String): Template {
        return javaClass.classLoader.getResourceAsStream(filePath)?.run {
            Mustache.compiler().compile(InputStreamReader(this))
        } ?: throw IllegalStateException("Template not found: $filePath")
    }

    fun generateFromTemplate(context: Map<String, Any>, outputDir: String, name: String, template: Template): File {
        return File(outputDir)
            .resolve(name)
            .apply {
                mustacheWriter(context, template, absolutePath)
                if (name.endsWith(".kt")) {
                    CopyUtils.kotlinFormat(this)
                }
            }
    }

    private fun mustacheWriter(context: Map<String, Any>, template: Template, filePath: String) {
        PrintWriter(OutputStreamWriter(FileOutputStream(filePath))).use {
            template.execute(context, it)
            it.flush()
        }
    }
}
