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

import org.slf4j.Logger
import org.web3j.openapi.codegen.config.ContractDetails
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File

class ContractModelGenerator(
    val packageName: String,
    val folderPath: String,
    val logger: Logger,
    val contractDetails: ContractDetails
) {
    fun generate() {
        File("$folderPath${File.separator}api${File.separator}model")
            .apply {
                mkdirs()
            }
        val context = setContext()
        copySources(context)
    }

    private fun setContext(): HashMap<String, Any> {
        return hashMapOf(
            "packageName" to packageName,
            "lowerCaseContractName" to contractDetails.lowerCaseContractName(),
            "capitalizedContractName" to contractDetails.capitalizedContractName()
        )
    }

    private fun copySources(context: HashMap<String, Any>) {
        File("codegen/src/main/resources/contracts/src/model")
            .listFiles()
            ?.forEach {
                logger.debug("Generating from ${it.canonicalPath}")
                TemplateUtils.generateFromTemplate(
                    context = context,
                    outputDir = "$folderPath${File.separator}api${File.separator}model",
                    template = TemplateUtils.mustacheTemplate(it.path.substringAfter("resources/")),
                    name = "${contractDetails.capitalizedContractName()}${it.name.removeSuffix(".mustache")
                        .removePrefix("Contract")}.kt"
                )
            }
    }
}
