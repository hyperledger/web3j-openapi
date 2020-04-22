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
package org.web3j.openapi.codegen.config

import org.jetbrains.kotlin.cli.common.repl.replAddLineBreak
import org.web3j.openapi.codegen.utils.ContractResource
import org.web3j.openapi.codegen.utils.Import
import org.web3j.protocol.core.methods.response.AbiDefinition

class ContractDetails(
    val contractName: String,
    val functionsDefintion: List<AbiDefinition>
) {
    fun lowerCaseContractName(): String {
        return contractName.toLowerCase()
    }

    fun capitalizedContractName(): String {
        return contractName.toLowerCase().capitalize()
    }

    fun deployParameters(): String{
        functionsDefintion
            .filter { it.type == "constructor" }
            .forEach {
                if(it.inputs.isNotEmpty()) return "${capitalizedContractName()}DeployParameters"
            }
        return "Void"
    }

    fun functionNames(): List<String> {
        return functionsDefintion
            .filter { it.type == "function" && it.inputs.isNotEmpty() }
            .map {
                it.name.capitalize()
            }
    }

    fun contractResources(): List<ContractResource> {
        val resources = mutableListOf<ContractResource>()
        functionsDefintion
            .filter { it.type == "function" }
            .forEach {
                val parameters =
                    if(it.inputs.isNotEmpty())
                        "${it.name.decapitalize()}Parameters : ${it.name.capitalize()}Parameters"
                    else ""
                resources.add(
                    ContractResource(
                        it.name.capitalize(),
                            "fun ${it.name}(${parameters}): TransactionReceipt",
                        if(it.inputs.isEmpty()) "GET" else "POST"
                    ))
            }
        return resources
    }
}
