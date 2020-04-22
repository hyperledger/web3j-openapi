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

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Utf8String
import java.math.BigInteger

object SolidityUtils {

    fun getNativeType(typeName: String): TypeName {
        return if (typeName == Address::class.java.simpleName) {
            String::class.asTypeName()
        } else if (typeName == "string") { // FIXME: Is this right ?
            String::class.asTypeName()
        } else if (typeName.startsWith("Uint") || typeName.startsWith("int")) {
            BigInteger::class.asTypeName()
        } else if (typeName == Utf8String::class.java.simpleName) {
            String::class.asTypeName()
        } else if (typeName.startsWith("Bytes") || typeName == "DynamicBytes") {
            ByteArray::class.asTypeName()
        } else if (typeName.startsWith("Bool")) {
            Boolean::class.asTypeName()
            // boolean cannot be a parameterized type
        } else if (typeName == org.web3j.abi.datatypes.primitive.Byte::class.java.simpleName) {
            Byte::class.asTypeName()
        } else if (typeName == org.web3j.abi.datatypes.primitive.Char::class.java.simpleName) {
            Char::class.asTypeName()
        } else if (typeName == org.web3j.abi.datatypes.primitive.Double::class.java.simpleName) {
            Double::class.asTypeName()
        } else if (typeName == org.web3j.abi.datatypes.primitive.Float::class.java.simpleName) {
            Float::class.asTypeName()
        } else if (typeName == org.web3j.abi.datatypes.primitive.Int::class.java.simpleName) {
            Int::class.asTypeName()
        } else if (typeName == org.web3j.abi.datatypes.primitive.Long::class.java.simpleName) {
            Long::class.asTypeName()
        } else if (typeName == org.web3j.abi.datatypes.primitive.Short::class.java.simpleName) {
            Short::class.asTypeName()
        } else if (typeName.startsWith("Int")) {
            BigInteger::class.asTypeName()
        } else {
            throw UnsupportedOperationException(
                "Unsupported type: $typeName, no native type mapping exists."
            )
        }
    }
}