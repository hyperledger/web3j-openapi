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
package org.web3j.openapi.codegen.utils

import org.web3j.openapi.codegen.config.ContractConfiguration
import org.web3j.openapi.codegen.config.ContractDetails
import java.io.File
import java.io.FileNotFoundException

object GeneratorUtils {

    fun loadContractConfigurations(abiList: List<File>, binList: List<File>): List<ContractConfiguration> {
        val abis = recurseIntoFolders(abiList, "abi")
        val bins = recurseIntoFolders(binList, "bin")
        return abis.map { abiFile ->
            val binFile = bins.find { bin ->
                bin.endsWith("${abiFile.name.removeSuffix(".abi")}.bin")
            } ?: throw FileNotFoundException("${abiFile.name.removeSuffix(".abi")}.bin")

            ContractConfiguration(
                abiFile,
                binFile,
                ContractDetails(
                    abiFile.name.removeSuffix(".abi"),
                    loadContractDefinition(abiFile) // TODO: Use the web3j.codegen function
                )
            )
        }
    }

    private fun recurseIntoFolders(list: List<File>, extension: String): List<File> {
        return list.flatMap { folder ->
            folder.walkTopDown().filter {
                it.extension == extension
            }.toList()
        }
    }
}
