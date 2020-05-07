package org.web3j.openapi.codegen.coregen.subgenerators


import mu.KLogging
import org.web3j.openapi.codegen.utils.KPoetUtils
import org.web3j.protocol.core.methods.response.AbiDefinition
import java.io.File

class CoreEventsModelGenerator(
    val packageName: String,
    private val contractName: String,
    private val eventName: String,
    private val folderPath: String,
    private val outputs: MutableList<AbiDefinition.NamedType>
) {
    fun generate() {
        val functionFile = KPoetUtils.inputsToDataClass(
            "$packageName.core.${contractName.toLowerCase()}.model",
            eventName,
            outputs,
            "EventResponse"
        )
        logger.debug("Generating $contractName $eventName model")
        functionFile.writeTo(File(folderPath))
    }

    companion object : KLogging()
}
