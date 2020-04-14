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

import java.io.File

object Folders {
    fun tempBuildFolder(): File {
        val tmpTestLocation = File(
            arrayOf(
                "build",
                "tmp",
                "testing",
                System.currentTimeMillis().toString()).joinToString(File.separator))
        if (!tmpTestLocation.mkdirs()) throw Exception(
            "Unable to create folder at " + tmpTestLocation.absolutePath)
        return tmpTestLocation
    }
}
