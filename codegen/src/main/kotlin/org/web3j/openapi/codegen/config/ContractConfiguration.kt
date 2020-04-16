package org.web3j.openapi.codegen.config

import org.web3j.openapi.codegen.contracts.ContractDetails
import java.io.File

data class ContractConfiguration(
    val abiFile: File,
    val contractDetails: ContractDetails
) {
}