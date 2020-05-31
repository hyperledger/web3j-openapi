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

import org.web3j.protocol.core.methods.response.AbiDefinition

data class ContractDetails(
    val contractName: String,
    val abiDefinitions: List<AbiDefinition>
) {
    val lowerCaseContractName: String
        get() = contractName.toLowerCase()

    val capitalizedContractName: String
        get() = contractName.capitalize()

    val decapitalizedContractName: String
        get() = contractName.decapitalize()

    val deployParameters: String
        get() = abiDefinitions
            .filter { it.type == "constructor" }
            .firstOrNull { it.inputs.isNotEmpty() }
            ?.run {
                "(parameters: ${capitalizedContractName}DeployParameters)"
            } ?: "()"
}
