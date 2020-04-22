package org.web3j.openapi.codegen.servergen.subgenerators

import com.squareup.kotlinpoet.*
import org.web3j.openapi.codegen.LICENSE
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.io.File

class ResourcesImplsGenerator(
    val packageName: String,
    private val contractName: String,
    private val functionsDefinition: List<AbiDefinition>,
    private val folderPath: String
) {

    fun generate() {
        generateClass().writeTo(File(folderPath))
    }

    private fun generateClass(): FileSpec {
        val resourcesFile = FileSpec.builder(
            "${packageName}.server.${contractName.decapitalize()}",
            "${contractName.capitalize()}ResourceImpl"
        )

        val contractClass = ClassName(
            "org.web3j.${contractName.decapitalize()}",
            contractName.capitalize()
        )

        val constructorBuilder = FunSpec.constructorBuilder()
            .addParameter(
                contractName.decapitalize(),
                contractClass
            )

        val contractResourceClass = ClassName(
            "${packageName}.core.${contractName.decapitalize()}",
            "${contractName.capitalize()}Resource")

        val resourcesClass = TypeSpec
            .classBuilder("${contractName.capitalize()}ResourceImpl")
            .primaryConstructor(constructorBuilder.build())
            .addProperty(
                PropertySpec.builder(
                    contractName.decapitalize(),
                    contractClass,
                    KModifier.PRIVATE
                )
                    .initializer(contractName.decapitalize())
                    .build()
            )
            .addSuperinterface(contractResourceClass)

        generateFunctions()
            .forEach {
                resourcesClass.addFunction(it)
            }

        return resourcesFile
            .addType(resourcesClass.build())
            .addComment(LICENSE)
            .build()

    }

    private fun generateFunctions(): List<FunSpec> {
        val functions = mutableListOf<FunSpec>()
        functionsDefinition
            .filter { it.type == "function" }
            .forEach {
                val funSpec = if(it.inputs.isEmpty()){
                    FunSpec.builder(it.name.decapitalize())
                        .returns(String::class.asTypeName())
                        .addCode(
                            "return ${contractName.decapitalize()}.${it.name.decapitalize()}().send()"
                        )
                        .addModifiers(KModifier.OVERRIDE)
                        .build()
                } else {
                    val nameClass = ClassName(
                        "${packageName}.core.${contractName.decapitalize()}.model",
                        "${it.name.capitalize()}Parameters"
                    )

                    FunSpec.builder(it.name.decapitalize())
                        .addParameter(
                            "${it.name.decapitalize()}Parameters",
                            nameClass
                        )
                        .returns(TransactionReceipt::class.asTypeName())
                        .addCode(
                            """
                                return ${contractName.decapitalize()}.${it.name.decapitalize()}(
                                    ${getCallParameters(it.inputs, it.name)}
                                ).send()
                            """.trimIndent()
                        )
                        .addModifiers(KModifier.OVERRIDE)
                        .build()
                }
                functions.add(funSpec)
            }
        return functions
    }

    private fun getCallParameters(inputs: MutableList<AbiDefinition.NamedType>, functionName: String) : String{
        var callParameters = ""
        inputs.forEach {
            callParameters += "${functionName.decapitalize()}Parameters.${it.name.decapitalize()},"
        }
        return callParameters.removeSuffix(",")
    }
}