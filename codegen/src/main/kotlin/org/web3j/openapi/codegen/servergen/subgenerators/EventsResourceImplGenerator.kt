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

import org.web3j.openapi.codegen.utils.GeneratorUtils.sanitizedName
import org.web3j.openapi.codegen.utils.TemplateUtils
import org.web3j.openapi.codegen.utils.structName
import org.web3j.protocol.core.methods.response.AbiDefinition

class EventsResourceImplGenerator(
    val packageName: String,
    val contractName: String,
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

    fun generate() {
        abiDefinitions
            .filter { it.type == "event" }
            .forEach { abiDefinition ->
                context["eventNameCap"] = abiDefinition.sanitizedName()!!.capitalize()
                context["eventName"] = abiDefinition.sanitizedName()!!.decapitalize()
                context["eventNameUp"] = abiDefinition.sanitizedName()!!.toUpperCase()
                context["args"] = getEventResponseParameters(abiDefinition)

                TemplateUtils.generateFromTemplate(
                    context = context,
                    outputDir = folderPath,
                    template = TemplateUtils.mustacheTemplate("server/src/contractImpl/NamedEventResourceImpl.mustache"),
                    name = "${abiDefinition.sanitizedName()!!.capitalize()}EventResourceImpl.kt"
                )
            }
    }

    private fun getEventResponseParameters(abiDef: AbiDefinition): String {
        return abiDef.inputs.joinToString(",") {
            if (it.components.isEmpty()) "it.${it.name}"
            else
                getStructEventParameters(it, abiDef.sanitizedName()!!, "it.${it.name}")
        }
    }

    private fun getStructEventParameters(input: AbiDefinition.NamedType, functionName: String, callTree: String = ""): String {
        val structName = input.internalType.structName
        val decapitalizedFunctionName = functionName.decapitalize() // FIXME: do we need this ?
        val parameters = input.components.joinToString(",") { component ->
            if (component.components.isNullOrEmpty()) "$callTree.${component.name}"
            else getStructEventParameters(component, decapitalizedFunctionName, "$callTree.${component.name}".removeSuffix("."))
        }
        return "$packageName.core.${contractName.toLowerCase()}.model.${structName}StructModel($parameters)" // FIXME: Are you sure about the lower case ?
    }
}
