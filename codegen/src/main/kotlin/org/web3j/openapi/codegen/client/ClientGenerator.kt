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
package org.web3j.openapi.codegen.client

import mu.KLogging
import org.web3j.openapi.codegen.DefaultGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File

class ClientGenerator(
    configuration: GeneratorConfiguration
) : DefaultGenerator(
    configuration
) {

    override fun generate() {
        val folderPath = CopyUtils.createTree("client", packageDir, configuration.outputDir)
        copyGradleFile(folderPath)
        copySources(folderPath)
    }

    private fun copyGradleFile(folderPath: String) {
        logger.debug("Copying client/build.gradle")
        CopyUtils.copyResource(
            "client/build.gradle",
            File(folderPath.substringBefore("client")))
    }

    private fun copySources(folderPath: String) {
        File("codegen/src/main/resources/client/src/")
            .listFiles()
            ?.forEach { it ->
                logger.debug("Generating from ${it.canonicalPath}")
                TemplateUtils.generateFromTemplate(
                    context = context,
                    outputDir = folderPath,
                    template = TemplateUtils.mustacheTemplate(it.path.substringAfter("resources/")),
                    name = "${it.name.removeSuffix(".mustache")}.kt"
                )
            }
    }

    companion object : KLogging()
}
