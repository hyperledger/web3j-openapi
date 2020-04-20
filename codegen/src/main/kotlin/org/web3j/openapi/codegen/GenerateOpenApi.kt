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
package org.web3j.openapi.codegen

import org.web3j.openapi.codegen.client.ClientGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.contracts.ContractsGenerator
import org.web3j.openapi.codegen.core.CoreGenerator
import org.web3j.openapi.codegen.gradle.GradleResourceCopy
import org.web3j.openapi.codegen.server.ServerGenerator
import org.web3j.openapi.codegen.utilsModule.UtilsGenerator
import java.io.File

class GenerateOpenApi(
    private val configuration: GeneratorConfiguration
) {
    fun generateAll() {
        generateGradleResources()
        generateUtils()
        generateClient()
        generateCore()
        generateContracts()
        generateServer()
    }

    fun generateClient() {
        val clientGenerator = ClientGenerator(configuration)
        clientGenerator.generate()
    }

    fun generateServer() {
        val serverGenerator = ServerGenerator(configuration)
        serverGenerator.generate()
    }

    fun generateCore() {
        val coreGenerator = CoreGenerator(configuration)
        coreGenerator.generate()
    }

    fun generateContracts() {
        val contractGenerator = ContractsGenerator(configuration)
        contractGenerator.generate()
    }

    fun generateGradleResources() {
        GradleResourceCopy.copyProjectResources(File(configuration.outputDir))
    }

    fun generateUtils() {
        val utilsGenerator = UtilsGenerator(configuration)
        utilsGenerator.generate()
    }
}