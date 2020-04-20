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
package org.web3j.openapi.codegen.server

import mu.KLogging
import org.web3j.openapi.codegen.DefaultGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File

class ServerGenerator(
    configuration: GeneratorConfiguration
) : DefaultGenerator(
    configuration
) {
    init {
        context["contracts"] = configuration.contracts
    }

    override val packageDir = configuration.packageName.split(".").joinToString("/")
    override val folderPath = CopyUtils.createTree("server", packageDir, configuration.outputDir)

    override fun generate() {
        copyGradleFile(folderPath)
        copyResources()
        copySources()
    }

    private fun copyGradleFile(folderPath: String) {
        logger.debug("Copying server/build.gradle")
        CopyUtils.copyResource(
            "server/build.gradle",
            File(folderPath.substringBefore("server"))
        )
    }

    private fun copyResources() {
        File("${folderPath.substringBefore("main")}${File.separator}main${File.separator}resources")
            .apply {
                mkdirs()
            }
        logger.debug("Copying server/resources")
        CopyUtils.copyResource(
            "server/src/main/resources/logback.xml",
            File(folderPath.substringBefore("server"))
        )
        CopyUtils.copyResource(
            "server/src/main/resources/logging.properties",
            File(folderPath.substringBefore("server"))
        )
    }

    private fun copySources() {
        File("codegen/src/main/resources/server/src/")
            .listFiles()
            .filter { !it.isDirectory }
            .forEach {
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
