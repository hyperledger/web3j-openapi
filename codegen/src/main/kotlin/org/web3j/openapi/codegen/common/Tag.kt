package org.web3j.openapi.codegen.common;

data class Tag (
        val name: String,
        val description: String,
        var lastComma: String = ","
)