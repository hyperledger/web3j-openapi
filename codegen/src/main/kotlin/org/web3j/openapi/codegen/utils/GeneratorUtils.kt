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
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File
import java.io.FileNotFoundException
import java.lang.IllegalStateException

object GeneratorUtils {

    fun loadContractConfigurations(abiList: List<File>, binList: List<File>): List<ContractConfiguration> {
        val abis = checkDuplicates(recurseIntoFolders(abiList, "abi"))
        val bins = recurseIntoFolders(binList, "bin").associateBy({ it.nameWithoutExtension }, { it })
        return abis.map { abiFile ->
            ContractConfiguration(
                abiFile,
                bins[abiFile.nameWithoutExtension]
                    ?: throw FileNotFoundException("${abiFile.nameWithoutExtension}.bin"),
                ContractDetails(
                    abiFile.name.removeSuffix(".abi"),
                    loadContractDefinition(abiFile) // TODO: Use the web3j.codegen function
                )
            )
        }
    }

    private fun checkDuplicates(files: List<File>): List<File> {
        return files.distinctBy { it.name }.also {
            if (it.size != files.size) throw IllegalStateException("Duplicate contracts names found!")
        }
    }

    private fun recurseIntoFolders(list: List<File>, extension: String): List<File> {
        return list.flatMap { folder -> folder.walkTopDown().filter { it.extension == extension }.toList() }
    }

    internal fun argumentName(name: String?, index: Int): String = if (name.isNullOrEmpty()) "input$index" else name

    fun handleDuplicateNames(abiDefinitions: List<AbiDefinition>, type: String): List<AbiDefinition> {
        val distinctAbis = mutableMapOf<String, AbiDefinition>()
        abiDefinitions.forEach { abiDefinition ->
            if (abiDefinition.type == type) {
                if (distinctAbis[abiDefinition.name.capitalize()] != null ||
                    distinctAbis[abiDefinition.name.decapitalize()] != null
                ) {
                    var counter = 2
                    while (distinctAbis["${abiDefinition.name}$counter"] != null) counter++
                    distinctAbis["${abiDefinition.name}$counter"] =
                        abiDefinition.also { abiDef -> abiDef.name += "&$counter" }
                } else {
                    distinctAbis[abiDefinition.name] = abiDefinition
                }
            }
        }
        return distinctAbis.map { duplicate -> duplicate.value }.toMutableList().apply {
            addAll(abiDefinitions.filter { abiDef -> abiDef.type != type })
        }
    }

    fun handleDuplicateInputNames(inputs: List<AbiDefinition.NamedType>): List<AbiDefinition.NamedType> {
        val distinctInputs = mutableMapOf<String, AbiDefinition.NamedType>()
        inputs.forEachIndexed { index, namedType ->
            if (distinctInputs[argumentName(namedType.name, index).capitalize()] != null) {
                inputs.first { input ->
                    input.name == namedType.name
                }.apply {
                    this.name = "${this.name}Dup"
                }
            } else {
                distinctInputs[argumentName(namedType.name, index).capitalize()] = namedType
            }
        }
        return inputs
    }

    fun AbiDefinition.sanitizedName(wrapperCall: Boolean = false): String {
        return when (true) {
            name == null -> "" // Case of a constructor
            wrapperCall -> name.substringBefore("&")
            else -> name.replace("&", "")
        }
    }
}
