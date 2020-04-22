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
        val functionFile = FileSpec.builder(
            packageName,
            "${name.capitalize()}$type"
        )

        val constructor = TypeSpec
            .classBuilder("${name.capitalize()}$type")
            .addModifiers(KModifier.DATA)

        val functionBuilder = FunSpec.constructorBuilder()

        inputs.forEach {
            functionBuilder.addParameter(
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
        constructor.primaryConstructor(functionBuilder.build())

        return functionFile
            .addType(constructor.build())
            .addComment(LICENSE)
            .build()
    }
}