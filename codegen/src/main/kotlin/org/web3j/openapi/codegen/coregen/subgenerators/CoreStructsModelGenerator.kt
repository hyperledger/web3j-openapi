package org.web3j.openapi.codegen.coregen.subgenerators

import org.web3j.openapi.codegen.utils.toDataClass
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File

class CoreStructsModelGenerator(
    val packageName: String,
    val contractName: String,
    val functionName: String,
    val folderPath: String,
    val components: List<AbiDefinition.NamedType>) {
    fun generate() {
        val functionFile = components.toDataClass(
            "$packageName.core.${contractName.toLowerCase()}.model",
            functionName,
            "StructModel",
            packageName,
            contractName
        )
        CoreFunctionsModelGenerator.logger.debug("Generating $contractName $functionName parameters")
        functionFile.writeTo(File(folderPath))
    }

}
