package org.web3j.openapi.codegen.contracts

class ContractDetails(
    private val contractName: String
) {
    fun lowerCaseContractName(): String{
        return contractName.toLowerCase()
    }

    fun capitalizedContractName():String{
        return contractName.toLowerCase().capitalize()
    }
}