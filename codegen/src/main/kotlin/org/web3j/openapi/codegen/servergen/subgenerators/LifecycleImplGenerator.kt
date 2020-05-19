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
package org.web3j.openapi.codegen.servergen.subgenerators

import mu.KLogging
import org.web3j.openapi.codegen.config.ContractDetails
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File

class LifecycleImplGenerator(
    val packageName: String,
    val folderPath: String,
    val contractDetails: ContractDetails
) {
    val context = mutableMapOf<String, Any>()

    init {
        context["packageName"] = packageName
        context["decapitalizedContractName"] = contractDetails.decapitalizedContractName()
        context["lowerCaseContractName"] = contractDetails.lowerCaseContractName()
        context["capitalizedContractName"] = contractDetails.capitalizedContractName()
        context["parameters"] = getParameters()
        context["deployParameters"] = contractDetails.deployParameters()
    }

    fun generate() {
        File("$folderPath")
            .apply {
                mkdirs()
            }
        copySources()
    }

    private fun getParameters(): String {
        if (contractDetails.deployParameters() == "()") return ""
        var parameters = ""
        contractDetails.functionsDefintion
            .filter { it.type == "constructor" }
            .map { it.inputs }
            .first()
            .forEach {
                parameters += ", parameters.${it.name}"
            }
        return parameters
    }

    private fun copySources() {
        TemplateUtils.generateFromTemplate(
            context = context,
            outputDir = folderPath,
            template = TemplateUtils.mustacheTemplate("server/src/contractImpl/ContractLifecycleImpl.mustache"),
            name = "${contractDetails.capitalizedContractName()}LifecycleImpl.kt"
        )
    }

    companion object : KLogging()
}
