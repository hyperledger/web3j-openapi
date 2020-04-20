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
package org.web3j.openapi.codegen.gradle

import org.web3j.openapi.codegen.client.ClientGenerator
import org.web3j.openapi.codegen.utils.CopyUtils
import java.io.File

object GradleResourceCopy {

    fun copyProjectResources(outputDir: File) {
        CopyUtils.copyResource("settings.gradle", outputDir)
        CopyUtils.copyResource("gradlew.bat", outputDir)
        CopyUtils.copyResource("gradlew", outputDir)
        CopyUtils.copyResource("build.gradle", outputDir)
        CopyUtils.copyResource("versions.properties", outputDir)

        File("${outputDir.toURI().path}${File.separator}gradlew").setExecutable(true)

        val gradleFolder = File("$outputDir${File.separator}gradle${File.separator}wrapper").apply { mkdirs() }
        CopyUtils.copyResource("gradle-wrapper.jar", gradleFolder)
        CopyUtils.copyResource(
            "gradle-wrapper.properties",
            gradleFolder
        )

        CopyUtils.copyResource("versions.gradle", File("$outputDir${File.separator}gradle"))
        CopyUtils.copyResource("README.md", outputDir)
    }

    fun copyModuleGradleFile(folderPath: String, module: String) {
        ClientGenerator.logger.debug("Copying ${module}/build.gradle")
        CopyUtils.copyResource(
            "${module}/build.gradle",
            File(folderPath.substringBefore(module)))
    }
}
