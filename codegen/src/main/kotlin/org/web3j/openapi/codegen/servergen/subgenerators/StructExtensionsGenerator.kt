package org.web3j.openapi.codegen.servergen.subgenerators

import com.squareup.kotlinpoet.*
import org.web3j.openapi.codegen.LICENSE
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.SolidityUtils
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File

class StructExtensionsGenerator(
    val packageName: String,
    private val contractName: String,
    private val resourcesDefinition: List<AbiDefinition>,
    private val folderPath: String) {

    fun generate() {
        generateExtensions().writeTo(File(folderPath))
        File(folderPath)
            .walkTopDown()
            .filter { file -> file.name.endsWith(".kt") }
            .forEach { file ->
                CopyUtils.kotlinFormat(file)
            }
    }

    private fun generateExtensions(): FileSpec {
        val extensionsFile = FileSpec.builder(
            "$packageName.server.${contractName.toLowerCase()}",
            "${contractName.capitalize()}Extensions"
        )

        SolidityUtils.extractStructs(resourcesDefinition)?.forEach {structDefinition ->
            val structName = SolidityUtils.getStructName(structDefinition!!.internalType)

            val contractClass = ClassName(
                "$packageName.wrappers.${contractName.capitalize()}",
                structName
            )

            val modelClass = ClassName(
                "$packageName.core.${contractName.toLowerCase()}.model",
                "${structName}StructModel"
            )

            val code = "return ${modelClass.simpleName}(${extensionDefinitionParameters(structDefinition)})"

            val extensionFunction = FunSpec.builder("toModel")
                .receiver(contractClass)
                .returns(modelClass)
                .addCode(code)
                .build()
            extensionsFile.addFunction(extensionFunction)
        }

        return extensionsFile
            .build()
    }

    private fun extensionDefinitionParameters(structDefinition: AbiDefinition.NamedType): String {
        return structDefinition.components.joinToString (",") {structField ->
            if(structField.components.isNullOrEmpty())
                structField.name
            else
                "${structField.name}.toModel()"
        }
    }
}