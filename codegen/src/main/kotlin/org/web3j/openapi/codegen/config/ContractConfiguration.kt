package org.web3j.openapi.codegen.config

import java.io.File

data class ContractConfiguration(
    val abiFile: File,
    val basePackageName: String
) {
}