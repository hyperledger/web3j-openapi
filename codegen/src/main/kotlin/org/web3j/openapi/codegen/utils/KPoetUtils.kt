package org.web3j.openapi.codegen.utils

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import org.web3j.openapi.codegen.LICENSE
import org.web3j.protocol.core.methods.response.AbiDefinition

object KPoetUtils {

    fun inputsToDataClass(packageName: String, name: String, inputs: MutableList<AbiDefinition.NamedType>, type: String): FileSpec {
        val outputFile = FileSpec.builder(
            packageName,
            "${name.capitalize()}$type"
        )

        val constructor = TypeSpec
            .classBuilder("${name.capitalize()}$type")
            .addModifiers(KModifier.DATA)

        val constructorBuilder = FunSpec.constructorBuilder()

        inputs.forEach {
            constructorBuilder.addParameter(
                it.name,
                SolidityUtils.getNativeType(it.type)
            )
            constructor.addProperty(
                PropertySpec.builder(
                    it.name,
                    SolidityUtils.getNativeType(it.type)
                )
                    .initializer(it.name)
                    .build()
            )
        }
        constructor.primaryConstructor(constructorBuilder.build())

        return outputFile
            .addType(constructor.build())
            .addComment(LICENSE)
            .build()
    }
}