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
import org.web3j.openapi.codegen.servergen.ServerGenerator

class OpenApiGenerator(
    private val configuration: GeneratorConfiguration,
) {
    fun generate() {
        println("Generating Web3j-OpenAPI project ... Files written to ${configuration.outputDir}")
        generateCore()
        if (configuration.withImplementations) generateServer()
    }

    private fun generateServer() {
        ServerGenerator(configuration).generate()
    }

    private fun generateCore() {
        CoreGenerator(configuration).generate()
    }
}
