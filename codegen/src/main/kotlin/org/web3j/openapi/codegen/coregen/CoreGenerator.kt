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
package org.web3j.openapi.codegen.coregen

import mu.KLogging
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty
import org.web3j.openapi.codegen.AbstractGenerator
import org.web3j.openapi.codegen.common.Import
import org.web3j.openapi.codegen.common.Tag
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.coregen.subgenerators.CoreApiGenerator
import org.web3j.openapi.codegen.gradlegen.GradleResourceCopy
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils.generateFromTemplate
import org.web3j.openapi.codegen.utils.TemplateUtils.mustacheTemplate
import java.io.FileNotFoundException
import java.nio.file.Path

class CoreGenerator(
    configuration: GeneratorConfiguration
) : AbstractGenerator(
    configuration
) {
    override fun generate() {
        if (configuration.contracts.isEmpty()) throw FileNotFoundException("No contracts found!")
        val folderPath = CopyUtils.createTree("core", packageDir, configuration.outputDir)
        GradleResourceCopy.copyModuleGradleFile(folderPath, "core")
        setContext()
        copySources(folderPath)

        configuration.contracts.forEach {
            logger.debug("Generating ${it.contractDetails.capitalizedContractName} api folders and files")
            CoreApiGenerator(
                configuration.packageName,
                folderPath = Path.of(
                    folderPath,
                    it.contractDetails.lowerCaseContractName
                ).toString(),
                contractDetails = it.contractDetails
            ).generate()
        }
    }

    private fun setContext() {
        context["contractsConfiguration"] = configuration.contracts
        context["apiImports"] = getApiImports()
        context["tags"] = getTags()
    }

    private fun getTags(): List<Tag> {
        return configuration.contracts.map {
            Tag(
                it.contractDetails.capitalizedContractName,
                "List ${it.contractDetails.capitalizedContractName} method's calls"
            )
        }.also {
            it.ifNotEmpty { last().lastCharacter = "" }
        }
    }

    private fun getApiImports(): List<Import> {
        return configuration.contracts.map {
            Import("import ${configuration.packageName}.core.${it.contractDetails.lowerCaseContractName}.${it.contractDetails.capitalizedContractName}")
        }
    }

    private fun copySources(folderPath: String) {
        generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = mustacheTemplate("core/src/ContractsApi.mustache"),
            name = "ContractsApi.kt"
        )
        generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = mustacheTemplate("core/src/GeneratedContractsResource.mustache"),
            name = "GeneratedContractsResource.kt"
        )
    }

    companion object : KLogging()
}
