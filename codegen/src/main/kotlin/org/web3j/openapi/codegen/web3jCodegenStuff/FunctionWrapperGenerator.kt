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
package org.web3j.openapi.codegen.web3jCodegenStuff

import org.web3j.codegen.Console
import org.web3j.tx.Contract
import java.io.File
import java.io.IOException

abstract class FunctionWrapperGenerator(
    open val contractClass: Class<out Contract?>,
    val destinationDirLocation: File?,
    open val basePackageName: String?,
    open val useJavaNativeTypes: Boolean,
    open val useJavaPrimitiveTypes: Boolean
) {

    @Throws(IOException::class, ClassNotFoundException::class)
    abstract fun generate()

    companion object {
        const val JAVA_TYPES_ARG = "--javaTypes"
        const val SOLIDITY_TYPES_ARG = "--solidityTypes"
        const val PRIMITIVE_TYPES_ARG = "--primitiveTypes"
        fun useJavaNativeTypes(argVal: String, usageString: String?): Boolean {
            var useJavaNativeTypes = true
            if (SOLIDITY_TYPES_ARG == argVal) {
                useJavaNativeTypes = false
            } else if (JAVA_TYPES_ARG == argVal) {
                useJavaNativeTypes = true
            } else {
                Console.exitError(usageString)
            }
            return useJavaNativeTypes
        }

        fun parsePositionalArg(args: Array<String?>?, idx: Int): String? {
            return if (args != null && args.size > idx) {
                args[idx]
            } else {
                ""
            }
        }

        fun parseParameterArgument(args: Array<String>, vararg parameters: String): String {
            for (parameter in parameters) {
                for (i in args.indices) {
                    if (args[i] == parameter && i + 1 < args.size) {
                        val parameterValue = args[i + 1]
                        if (!parameterValue.startsWith("-")) {
                            return parameterValue
                        }
                    }
                }
            }
            return ""
        }

        fun getFileNameNoExtension(fileName: String): String {
            val splitName = fileName.split("\\.(?=[^.]*$)".toRegex()).toTypedArray()
            return splitName[0]
        }
    }
}
