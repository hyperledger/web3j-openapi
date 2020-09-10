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
import org.web3j.openapi.codegen.gradlegen.GradleResourceCopy.generateGradleBuildFile
import org.web3j.openapi.codegen.servergen.subgenerators.EventsResourceImplGenerator
import org.web3j.openapi.codegen.servergen.subgenerators.LifecycleImplGenerator
import org.web3j.openapi.codegen.servergen.subgenerators.ResourcesImplGenerator
import org.web3j.openapi.codegen.servergen.subgenerators.StructExtensionsGenerator
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

internal class ServerGenerator(
    configuration: GeneratorConfiguration
) : AbstractGenerator(
    configuration
) {
    private val serverImports: List<Import> by lazy {
        configuration.contracts.map {
            Import("import ${configuration.packageName}.server.${it.contractDetails.lowerCaseContractName}.${it.contractDetails.capitalizedContractName}")
        }
    }

    init {
        context["contracts"] = configuration.contracts
        context["serverImports"] = serverImports
        context["projectName"] = configuration.sanitizedProjectName.capitalize()
        context["rootProjectName"] = configuration.rootProjectName
        context["version"] = configuration.version
    }

    override fun generate() {
        if (configuration.contracts.isEmpty()) throw FileNotFoundException("No contracts found!")

        // FolderPath contains the module output directory
        val folderPath = CopyUtils.createTree(configuration.outputDir, packageDir, configuration.withGradleResources, "server")

        // outputDir is the project root directory
        val outputDir = if (configuration.withGradleResources) Paths.get(
            folderPath.substringBefore("kotlin"),
            "kotlin"
        ).toString()
        else folderPath.substringBefore(configuration.packageName.substringBefore("."))

        if (configuration.withServerBuildFile)
            generateGradleBuildFile(
                if (configuration.withGradleResources)
                    folderPath.substringBefore("src")
                else
                    folderPath,
                "server", context)
        copyResources(folderPath)
        copySources(folderPath)

        configuration.contracts.forEach {
            logger.debug("Generating ${it.contractDetails.capitalizedContractName} server folders and files")
            LifecycleImplGenerator(
                packageName = configuration.packageName,
                folderPath = Paths.get(
                    folderPath,
                    it.contractDetails.lowerCaseContractName
                ).toString(),
                contractDetails = it.contractDetails
            ).generate()

            ResourcesImplGenerator(
                packageName = configuration.packageName,
                contractName = it.contractDetails.contractName,
                folderPath = outputDir,
                resourcesDefinition = it.contractDetails.abiDefinitions
            ).generate()

            EventsResourceImplGenerator(
                packageName = configuration.packageName,
                contractName = it.contractDetails.contractName,
                folderPath = Paths.get(
                    folderPath,
                    it.contractDetails.lowerCaseContractName
                ).toString(),
                abiDefinitions = it.contractDetails.abiDefinitions
            ).generate()

            StructExtensionsGenerator(
                packageName = configuration.packageName,
                contractName = it.contractDetails.contractName,
                folderPath = outputDir,
                resourcesDefinition = it.contractDetails.abiDefinitions
            ).generate()
        }
        if (configuration.withGradleResources)
            TemplateUtils.generateFromTemplate(
                context = mapOf(Pair("projectName", configuration.projectName.toLowerCase())),
                outputDir = configuration.outputDir,
                template = TemplateUtils.mustacheTemplate("server/Dockerfile.mustache"),
                name = "Dockerfile"
            )
    }

    private fun copyResources(folderPath: String) {
        // FIXME: Not needed if we won't copy the logging.properties
//        File(
//            Paths.get(
//                folderPath.substringBefore("main"),
//                "main",
//                "resources"
//            ).toString()
//        ).apply {
//            mkdirs()
//        }
//        logger.debug("Copying server/resources")
        // FIXME: Throws exception (java.nio.file.NoSuchFileException) when running the integration test generation
//        CopyUtils.copyResource(
//            "server/src/main/resources/logging.properties",
//            File(folderPath.substringBefore("server"))
//        )

        // FIXME Copies SPI resource in main
        val spiFolder = File(
            if (configuration.withGradleResources) Paths.get(
                folderPath.substringBefore("server"),
                "server",
                "src",
                "main",
                "resources",
                "META-INF",
                "services"
            ).toString()
        else Paths.get(
                folderPath.substringBefore("kotlin"),
                "resources",
                "META-INF",
                "services"
            ).toString()
        ).apply { mkdirs() }
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = spiFolder.absolutePath,
            template = TemplateUtils.mustacheTemplate(
                "server/src/main/resources/META-INF/services/org.web3j.openapi.server.spi.OpenApiResourceProvider.mustache"),
            name = "org.web3j.openapi.server.spi.OpenApiResourceProvider"
        )
    }

    private fun copySources(folderPath: String) {
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("server/src/ContractsApiImpl.mustache"),
            name = "${configuration.sanitizedProjectName.capitalize()}ApiImpl.kt"
        )
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("server/src/ContractsResourceProvider.mustache"),
            name = "${configuration.sanitizedProjectName.capitalize()}ResourceProvider.kt"
        )
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("server/src/GeneratedContractsResourceImpl.mustache"),
            name = "${configuration.sanitizedProjectName.capitalize()}ResourceImpl.kt"
        )
    }

    companion object : KLogging()
}
