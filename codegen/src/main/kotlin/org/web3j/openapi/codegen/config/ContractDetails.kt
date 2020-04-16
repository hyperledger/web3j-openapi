package org.web3j.openapi.codegen.config

import org.web3j.protocol.core.methods.response.AbiDefinition

class ContractDetails(
    val contractName: String,
    val functionsDefintion: List<AbiDefinition>
) {
    fun lowerCaseContractName(): String{
        return contractName.toLowerCase()
    }

    fun capitalizedContractName():String{
        return contractName.toLowerCase().capitalize()
    }
}