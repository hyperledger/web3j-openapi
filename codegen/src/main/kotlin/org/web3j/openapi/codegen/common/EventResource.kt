package org.web3j.openapi.codegen.common

data class EventResource(
    val resource: String = "",
    val returnType: String = "",
    val path: String = "",
    val capitalizedName: String,
    val decapitalizedName: String = capitalizedName.decapitalize()
)