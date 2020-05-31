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
package org.web3j.openapi.codegen.gradlegen

import mu.KLogging
import org.web3j.openapi.codegen.utils.CopyUtils.copyResource
import java.io.File

object GradleResourceCopy : KLogging() {

    fun copyProjectResources(outputDir: File) {
        copyResource("settings.gradle", outputDir)
        copyResource("gradlew.bat", outputDir)
        copyResource("gradlew", outputDir)
        copyResource("build.gradle", outputDir)
        copyResource("versions.properties", outputDir)

        File("${outputDir.toURI().path}${File.separator}gradlew").setExecutable(true)

        val gradleFolder = File("$outputDir${File.separator}gradle${File.separator}wrapper").apply { mkdirs() }
        copyResource("gradle-wrapper.jar", gradleFolder)
        copyResource(
            "gradle-wrapper.properties",
            gradleFolder
        )

        copyResource("versions.gradle", File("$outputDir${File.separator}gradle"))
        copyResource("README.md", outputDir)
    }

    fun copyModuleGradleFile(folderPath: String, module: String) {
        logger.debug("Copying $module/build.gradle")
        copyResource(
            "$module/build.gradle",
            File(folderPath.substringBefore(module))
        )
    }
}
