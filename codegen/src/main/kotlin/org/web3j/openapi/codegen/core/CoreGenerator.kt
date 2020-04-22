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
package org.web3j.openapi.codegen.core

import mu.KLogging
import org.web3j.openapi.codegen.DefaultGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.contracts.ContractsGenerator
import org.web3j.openapi.codegen.core.subgenerators.CoreApiGenerator
import org.web3j.openapi.codegen.gradle.GradleResourceCopy
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.Import
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File
import java.nio.file.Path

class CoreGenerator(
    configuration: GeneratorConfiguration
) : DefaultGenerator(
    configuration
) {
    override fun generate() {
        val folderPath = CopyUtils.createTree("core", packageDir, configuration.outputDir)
        GradleResourceCopy.copyModuleGradleFile(folderPath, "core")
        setContext()
        copySources(folderPath)

        configuration.contracts.forEach {
            ContractsGenerator.logger.debug("Generating ${it.contractDetails.capitalizedContractName()} api folders and files")
            CoreApiGenerator(
                configuration.packageName,
                folderPath = Path.of(
                    folderPath,
                    it.contractDetails.lowerCaseContractName()
                ).toString(),
                contractDetails = it.contractDetails
            ).generate()
        }
    }

    private fun setContext() {
        context["contractsConfiguration"] = configuration.contracts
        context["apiImports"] = getApiImports()
    }

    private fun getApiImports(): List<Import> {
        return configuration.contracts.map {
            Import("import ${configuration.packageName}.core.${it.contractDetails.lowerCaseContractName()}.${it.contractDetails.capitalizedContractName()}")
        }
    }

    private fun copySources(folderPath: String) {
        File("codegen/src/main/resources/core/src/")
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
