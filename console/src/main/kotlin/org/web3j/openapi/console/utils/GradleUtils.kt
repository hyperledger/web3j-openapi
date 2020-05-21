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
package org.web3j.openapi.console.utils

import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ResultHandler
import org.web3j.openapi.console.GenerateCmd
import java.io.File
import java.io.OutputStream

object GradleUtils {
    fun runGradleTask(projectFolder: File, task: String, description: String, outputStream : OutputStream? = null) {
        println("$description\n")
        GradleConnector.newConnector()
            .useBuildDistribution()
            .forProjectDirectory(projectFolder)
            .connect()
            .apply {
                newBuild()
                    .forTasks(task)
                    .setStandardOutput(outputStream)
                    .run(object : ResultHandler<Void> {
                        override fun onFailure(failure: GradleConnectionException) {
                            GenerateCmd.logger.debug(failure.message) // FIXME: throw information concerning this failure
                            throw GradleConnectionException(failure.message)
                        }

                        override fun onComplete(result: Void?) {
                        }
                    })
                close()
            }
    }
}
