package org.web3j.openapi.console.utils

import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.GradleConnector
import org.gradle.tooling.ResultHandler
import org.web3j.openapi.console.GenerateCmd
import java.io.File

object GradleUtils {
    fun runGradleTask(projectFolder: File, task: String, description: String) {
        println(description)
        GradleConnector.newConnector()
            .useBuildDistribution()
            .forProjectDirectory(projectFolder)
            .connect()
            .apply {
                newBuild()
                    .forTasks(task)
                    .run(object : ResultHandler<Void> {
                        override fun onFailure(failure: GradleConnectionException) {
                            GenerateCmd.logger.debug(failure.message)
                            throw GradleConnectionException(failure.message)
                        }

                        override fun onComplete(result: Void?) {
                        }
                    })
                close()
            }
    }
}