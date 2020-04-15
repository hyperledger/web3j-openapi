package org.web3j.openapi.codegen.web3jCodegenStuff

import com.fasterxml.jackson.databind.ObjectMapper
import org.web3j.codegen.Console
import org.web3j.codegen.Console.exitError
import org.web3j.codegen.SolidityFunctionWrapper
import org.web3j.tx.Contract
import java.io.File
import java.io.IOException

class SolidityFunctionWrapperGenerator(
    val binFile: File? = null,
    val abiFile: File? = null,
    val destinationDir: File?,
    var contractName: String? = null,
    override val basePackageName: String?,
    override val useJavaNativeTypes: Boolean = false,
    override val useJavaPrimitiveTypes: Boolean = false,
    val generateSendTxForCalls: Boolean = false,
    override val contractClass: Class<out Contract?> = Contract::class.java,
    val addressLength: Int = 0
) : FunctionWrapperGenerator(
    contractClass,
    destinationDir,
    basePackageName,
    useJavaNativeTypes,
    useJavaPrimitiveTypes) {

    private val COMMAND_SOLIDITY = "solidity"
    private val COMMAND_GENERATE = "generate"
    val COMMAND_PREFIX = "$COMMAND_SOLIDITY $COMMAND_GENERATE"
    private val solidityTypes = false
    val destinationFileDir: File? = destinationDir
    val packageName: String? = basePackageName
    private val primitiveTypes = useJavaPrimitiveTypes

    /*
    Class<? extends Contract> contractClass
     * Usage: solidity generate [-hV] [-jt] [-st] -a=<abiFile> [-b=<binFile>]
     * -o=<destinationFileDir> -p=<packageName>
     * -h, --help                 Show this help message and exit.
     * -V, --version              Print version information and exit.
     * -a, --abiFile=<abiFile>    abi file with contract definition.
     * -b, --binFile=<binFile>    bin file with contract compiled code in order to
     * generate deploy methods.
     * -o, --outputDir=<destinationFileDir>
     * destination base directory.
     * -p, --package=<packageName>
     * base package name.
     * -jt, --javaTypes       use native java types.
     * Default: true
     * -st, --solidityTypes   use solidity types.
     */


    @Throws(IOException::class)
    fun loadContractDefinition(absFile: File?): List<org.web3j.protocol.core.methods.response.AbiDefinition> {
        val objectMapper: ObjectMapper =
            org.web3j.protocol.ObjectMapperFactory.getObjectMapper()
        val abiDefinition: Array<org.web3j.protocol.core.methods.response.AbiDefinition> =
            objectMapper.readValue(
                absFile,
                Array<org.web3j.protocol.core.methods.response.AbiDefinition>::class.java
            )
        return listOf(*abiDefinition)
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    override fun generate() {
        var binary: String? = Contract.BIN_NOT_PROVIDED
        if (binFile != null) {
            val bytes: ByteArray = org.web3j.utils.Files.readBytes(binFile)
            binary = String(bytes)
        }
        val functionDefinitions: List<org.web3j.protocol.core.methods.response.AbiDefinition> =
            loadContractDefinition(abiFile)
        if (functionDefinitions.isEmpty()) {
            exitError("Unable to parse input ABI file")
        } else {
            val className: String = org.web3j.utils.Strings.capitaliseFirstLetter(contractName)
            print("Generating " + basePackageName.toString() + "." + className + " ... ")
            SolidityFunctionWrapper(
                useJavaNativeTypes,
                useJavaPrimitiveTypes,
                generateSendTxForCalls,
                addressLength
            )
                .generateJavaFiles(
                    contractClass,
                    contractName,
                    binary,
                    functionDefinitions,
                    destinationDirLocation.toString(),
                    basePackageName,
                    null
                )
            println(
                """
                    File written to ${destinationDirLocation.toString().toString()}
                    
                    """.trimIndent()
            )
        }
    }

    fun run() {
        try {
            val useJavaTypes: Boolean = !solidityTypes!!
            if (contractName.isNullOrEmpty()) {
                contractName = FunctionWrapperGenerator.getFileNameNoExtension(abiFile!!.name)
            }
            SolidityFunctionWrapperGenerator(
                binFile,
                abiFile,
                destinationFileDir,
                contractName,
                packageName,
                useJavaTypes,
                primitiveTypes,
                addressLength = addressLength
            )
                .generate()
        } catch (e: Exception) {
            Console.exitError(e)
        }
    }
}