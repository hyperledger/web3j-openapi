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