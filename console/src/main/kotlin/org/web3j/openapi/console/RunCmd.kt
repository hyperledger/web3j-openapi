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
package org.web3j.openapi.console

import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ResultHandler
import picocli.CommandLine
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.concurrent.Callable

@CommandLine.Command(name = "run",
    description = ["Generates then runs a web3j-openapi project"])
class RunCmd : OpenApiCli(), Callable<Int> {
    override fun call(): Int {
        generate()
        val projectFolder = File(
            Path.of(
                outputDirectory,
                projectName
            ).toString()
        ).apply {
            if (!exists()) throw FileNotFoundException(absolutePath)
        }

        GradleConnector.newConnector()
            .useBuildDistribution()
            .forProjectDirectory(projectFolder)
            .connect()
            .newBuild()
            .forTasks("runShadow")
            .run(object : ResultHandler<Void> {
                override fun onFailure(failure: GradleConnectionException) {
                    throw GradleConnectionException(failure.message)
                }

                override fun onComplete(result: Void) {}
            })
        return 0
    }
}
