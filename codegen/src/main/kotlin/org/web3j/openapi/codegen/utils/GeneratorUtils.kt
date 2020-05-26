package org.web3j.openapi.codegen.utils

import org.web3j.openapi.codegen.config.ContractConfiguration
import org.web3j.openapi.codegen.config.ContractDetails
import java.io.File
import java.io.FileNotFoundException

object GeneratorUtils {

    fun getContractsConfiguration(abisList: List<File>, binsList: List<File>): List<ContractConfiguration> {
        val abis = recurseIntoFolders(abisList, "abi")
        val bins = recurseIntoFolders(binsList, "bin")
        val contractsConfig = mutableListOf<ContractConfiguration>()
        abis.forEach { abiFile ->
            val bin = bins.find { bin ->
                bin.endsWith("${abiFile.name.removeSuffix(".abi")}.bin")
            } ?: throw FileNotFoundException("${abiFile.name.removeSuffix(".abi")}.bin")

            contractsConfig.add(
                ContractConfiguration(
                    abiFile,
                    bin,
                    ContractDetails(
                        abiFile.name.removeSuffix(".abi"),
                        SolidityUtils.loadContractDefinition(abiFile) // TODO: Use the web3j.codegen function
                    )
                )
            )
        }
        return contractsConfig
    }

    private fun recurseIntoFolders(list: List<File>, extension: String): List<File> {
        val recs = mutableListOf<File>()
        list.forEach { folder ->
            recs.addAll(folder.walkTopDown().filter { file -> file.extension == extension })
        }
        return recs
    }
}