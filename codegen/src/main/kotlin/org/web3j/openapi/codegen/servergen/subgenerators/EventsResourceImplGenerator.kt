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

import org.web3j.openapi.codegen.utils.TemplateUtils
import org.web3j.protocol.core.methods.response.AbiDefinition

class EventsResourceImplGenerator(
    packageName: String,
    contractName: String,
    private val folderPath: String,
    private val abiDefinitions: List<AbiDefinition>
) {

    private val context = mutableMapOf<String, Any>()

    init {
        context["packageName"] = packageName
        context["contractName"] = contractName.decapitalize()
        context["contractNameCap"] = contractName.capitalize()
        context["lowerCaseContractName"] = contractName.toLowerCase()
    }

    private fun args(inputs: MutableList<AbiDefinition.NamedType>): String { // TODO: will this support structs ?
        return inputs.joinToString(",") { "it.${it.name}" }
    }

    fun generate() {
        abiDefinitions
            .filter { it.type == "event" }
            .forEach { abiDefinition ->
                context["eventNameCap"] = abiDefinition.name.capitalize()
                context["eventName"] = abiDefinition.name.decapitalize()
                context["eventNameUp"] = abiDefinition.name.toUpperCase()
                context["args"] = args(abiDefinition.inputs)

                TemplateUtils.generateFromTemplate(
                    context = context,
                    outputDir = folderPath,
                    template = TemplateUtils.mustacheTemplate("server/src/contractImpl/NamedEventResourceImpl.mustache"),
                    name = "${abiDefinition.name.capitalize()}EventResourceImpl.kt"
                )
            }
    }
}
