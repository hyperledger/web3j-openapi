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
package org.web3j.openapi.codegen.utils

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.web3j.protocol.core.methods.response.AbiDefinition.NamedType

internal fun List<NamedType>.toDataClass(
    packageName: String,
    name: String,
    type: String
): FileSpec {
    val outputFile = FileSpec.builder(
        packageName,
        "${name.capitalize()}$type"
    )

    val constructor = TypeSpec
        .classBuilder("${name.capitalize()}$type")

    // FIXME: Events with no parameters require no field class
    if (isNotEmpty()) constructor.addModifiers(KModifier.DATA)

    val constructorBuilder = FunSpec.constructorBuilder()

    forEachIndexed { index, input ->
        val inputName = input.name ?: "input$index"
        constructorBuilder.addParameter(
            inputName,
            input.type.toNativeType()
        )
        constructor.addProperty(
            PropertySpec.builder(
                inputName,
                input.type.toNativeType()
            ).initializer(inputName).build()
        )
    }
    constructor.primaryConstructor(constructorBuilder.build())

    return outputFile
        .addType(constructor.build())
        .build()
}
