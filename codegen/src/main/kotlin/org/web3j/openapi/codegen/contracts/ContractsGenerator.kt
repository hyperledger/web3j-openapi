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
package org.web3j.openapi.codegen.contracts

import mu.KLogging
import org.web3j.openapi.codegen.DefaultGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.Import
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File

class ContractsGenerator(
    configuration: GeneratorConfiguration
) : DefaultGenerator(
    configuration
) {
    override val packageDir = configuration.packageName.split(".").joinToString("/")
    override val folderPath = CopyUtils.createTree("contracts", packageDir, configuration.outputDir)

    override fun generate() {
        copyGradleFile()
        val context = setContext()
        copySources(context)
        copyResources()

        configuration.contracts.forEach {
            logger.debug("Generating ${it.contractDetails.capitalizedContractName()} folders and files")
            File("$folderPath${File.separator}${it.contractDetails.lowerCaseContractName()}")
                .apply {
                    mkdirs()
                }
            ContractApiGenerator(
                configuration.packageName,
                folderPath = "$folderPath${File.separator}${it.contractDetails.lowerCaseContractName()}",
                logger = logger,
                contractDetails = it.contractDetails
            ).generate()
            ContractServerGenerator(
                configuration.packageName,
                folderPath = "$folderPath${File.separator}${it.contractDetails.lowerCaseContractName()}",
                logger = logger,
                contractDetails = it.contractDetails).generate()
            ContractModelGenerator(
                configuration.packageName,
                folderPath = "$folderPath${File.separator}${it.contractDetails.lowerCaseContractName()}",
                logger = logger,
                contractDetails = it.contractDetails).generate()
        }
    }

    private fun copyResources() {
        File("${folderPath.substringBefore("main")}${File.separator}main${File.separator}resources")
            .apply {
                mkdirs()
            }
        logger.debug("Copying contracts/resources")
        CopyUtils.copyResource(
            "contracts/src/main/resources/logback.xml",
            File(folderPath.substringBefore("contracts"))
        )
        CopyUtils.copyResource(
            "contracts/src/main/resources/logging.properties",
            File(folderPath.substringBefore("contracts"))
        )
    }

    private fun setContext(): HashMap<String, Any> {
        return hashMapOf(
            "packageName" to configuration.packageName,
            "contractsConfiguration" to configuration.contracts,
            "apiImports" to getApiImports(),
            "serverImports" to getServerImports()
        )
    }

    private fun getApiImports(): List<Import> {
        return configuration.contracts.map {
            Import("import ${configuration.packageName}.contracts.${it.contractDetails.lowerCaseContractName()}.api.${it.contractDetails.capitalizedContractName()}")
        }
    }

    private fun getServerImports(): List<Import> {
        return configuration.contracts.map {
            Import("import ${configuration.packageName}.contracts.${it.contractDetails.lowerCaseContractName()}.server.${it.contractDetails.capitalizedContractName()}")
        }
    }

    private fun copyGradleFile() {
        logger.debug("Copying contracts/build.gradle")
        CopyUtils.copyResource(
            "contracts/build.gradle",
            File(folderPath.substringBefore("contracts"))
        )
    }

    private fun copySources(context: HashMap<String, Any>) {
        File("codegen/src/main/resources/contracts/src/")
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
