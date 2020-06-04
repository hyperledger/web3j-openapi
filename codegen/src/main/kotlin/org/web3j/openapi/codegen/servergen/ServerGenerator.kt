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
package org.web3j.openapi.codegen.servergen

import mu.KLogging
import org.web3j.openapi.codegen.AbstractGenerator
import org.web3j.openapi.codegen.common.Import
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.servergen.subgenerators.LifecycleImplGenerator
import org.web3j.openapi.codegen.servergen.subgenerators.ResourcesImplGenerator
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

internal class ServerGenerator(
    configuration: GeneratorConfiguration
) : AbstractGenerator(
    configuration
) {
    init {
        context["contracts"] = configuration.contracts
        context["serverImports"] = getServerImports()
        context["projectName"] = configuration.projectName
        context["outputDir"] = configuration.jarDir.absolutePath
    }

    override fun generate() {
        if (configuration.contracts.isEmpty()) throw FileNotFoundException("No contracts found!")
        val folderPath = CopyUtils.createTree("server", packageDir, configuration.outputDir)
        copyGradleFile(folderPath)
        copyResources(folderPath)
        copySources(folderPath)

        configuration.contracts.forEach {
            logger.debug("Generating ${it.contractDetails.capitalizedContractName} server folders and files")
            LifecycleImplGenerator(
                packageName = configuration.packageName,
                folderPath = Path.of(
                    folderPath,
                    it.contractDetails.lowerCaseContractName
                ).toString(),
                contractDetails = it.contractDetails
            ).generate()

            ResourcesImplGenerator(
                packageName = configuration.packageName,
                contractName = it.contractDetails.contractName,
                folderPath = Path.of(
                    folderPath.substringBefore("kotlin"),
                    "kotlin"
                ).toString(),
                resourcesDefinition = it.contractDetails.abiDefinitions
            ).generate()
        }
    }

    private fun getServerImports(): List<Import> {
        return configuration.contracts.map {
            Import("import ${configuration.packageName}.server.${it.contractDetails.lowerCaseContractName}.${it.contractDetails.capitalizedContractName}")
        }
    }

    private fun copyGradleFile(folderPath: String) {
        logger.debug("Generating server/build.gradle")
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath.substringBefore("src"),
            template = TemplateUtils.mustacheTemplate("server/build.gradle.mustache"),
            name = "build.gradle"
        )
    }

    private fun copyResources(folderPath: String) {
        File(
            Path.of(
                folderPath.substringBefore("main"),
                "main",
                "resources"
            ).toString()
        ).apply {
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

        val spiFolder = File(
            Path.of(
                folderPath.substringBefore("server"),
                "server",
                "src",
                "main",
                "resources",
                "META-INF",
                "services"
            ).toString()
        ).apply { mkdirs() }
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = spiFolder.absolutePath,
            template = TemplateUtils.mustacheTemplate(
                    Path.of(
                        "server",
                        "src",
                        "main",
                        "resources",
                        "META-INF",
                        "services",
                        "org.web3j.openapi.core.spi.OpenApiResourceProvider.mustache"
                    ).toString()
            ),
            name = "org.web3j.openapi.core.spi.OpenApiResourceProvider"
        )
    }

    private fun copySources(folderPath: String) {
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("server/src/ContractsApiImpl.mustache"),
            name = "ContractsApiImpl.kt"
        )
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("server/src/ContractsResourceProvider.mustache"),
            name = "ContractsResourceProvider.kt"
        )
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("server/src/GeneratedContractsResourceImpl.mustache"),
            name = "GeneratedContractsResourceImpl.kt"
        )
    }

    companion object : KLogging()
}
