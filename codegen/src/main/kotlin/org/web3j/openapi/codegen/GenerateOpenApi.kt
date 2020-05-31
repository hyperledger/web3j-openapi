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

import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.coregen.CoreGenerator
import org.web3j.openapi.codegen.gradlegen.GradleResourceCopy
import org.web3j.openapi.codegen.servergen.ServerGenerator
import org.web3j.openapi.codegen.web3jCodegenStuff.SolidityFunctionWrapperGenerator
import java.io.File
import java.nio.file.Path

class GenerateOpenApi(
    private val configuration: GeneratorConfiguration
) {
    fun generateAll() {
        generateGradleResources()
        generateCore()
        generateServer()
        generateWrappers()
    }

    fun generateServer() {
        ServerGenerator(configuration).generate()
    }

    fun generateCore() {
        CoreGenerator(configuration).generate()
    }

    fun generateGradleResources() {
        GradleResourceCopy.copyProjectResources(File(configuration.outputDir))
    }

    fun generateWrappers() {
        configuration.contracts.forEach {
            SolidityFunctionWrapperGenerator(
                abiFile = it.abiFile,
                binFile = it.binFile,
                contractName = it.abiFile.name.removeSuffix(".abi"),
                basePackageName = "${configuration.packageName}.wrappers",
                destinationDir = File(
                    Path.of(
                        configuration.outputDir,
                        "server",
                        "src",
                        "main",
                        "java"
                    ).toString()
                ),
                useJavaPrimitiveTypes = true,
                useJavaNativeTypes = true,
                addressLength = configuration.addressLength
            ).generate()
        }
    }
}
