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
package org.web3j.openapi.codegen.generators

import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ResultHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.GeneratorUtils.loadContractConfigurations
import java.io.File
import java.io.IOException
import java.nio.file.Paths

class GenerationTest {

    @TempDir
    lateinit var tempFolder: File

    @Test
    fun `Generated project gradle tasks test`() {
        val contractsFolder = Paths.get(
            "src",
            "test",
            "resources",
            "contracts").toFile()

        val generatorConfiguration = GeneratorConfiguration(
            "testProject",
            "com.test",
            tempFolder.canonicalPath,
            loadContractConfigurations(
                listOf(contractsFolder), listOf(contractsFolder)
            ),
            160,
            "test",
            "0.0.2"
        )

        // FIXME: we should find a way to publish the current version of the generator and test the generator against it
//        assertDoesNotThrow { GenerateOpenApi(generatorConfiguration).generateAll() }
//        assertDoesNotThrow { GenerateOpenApi(generatorConfiguration).generateSwaggerUI() }
//
//        assertThat {
//            runGradleTask(
//                tempFolder,
//                "shadowJar")
//        }.isSuccess()
//
//        assertThat {
//            runGradleTask(
//                tempFolder,
//                "installDist")
//        }.isSuccess()
    }

    @Throws(IOException::class)
    private fun runGradleTask(projectFolder: File, task: String) {
        GradleConnector.newConnector()
            .useBuildDistribution()
            .forProjectDirectory(projectFolder)
            .connect()
            .apply {
                newBuild()
                    .forTasks(task)
                    .setStandardOutput(System.out)
                    .run(object : ResultHandler<Void> {
                        override fun onFailure(failure: GradleConnectionException) {
                            throw failure
                        }

                        override fun onComplete(result: Void?) {
                        }
                    })
                close()
            }
    }
}
