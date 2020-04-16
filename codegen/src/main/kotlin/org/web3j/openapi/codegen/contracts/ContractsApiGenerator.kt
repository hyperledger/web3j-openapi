package org.web3j.openapi.codegen.contracts

import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File
import org.slf4j.Logger

class ContractsApiGenerator(
    val configuration: GeneratorConfiguration,
    val folderPath: String,
    val logger: Logger
) {
    fun generate() {
        File("$folderPath${File.separator}api${File.separator}model")
            .apply {
                mkdirs()
            }
        val context = setContext()
        copySources(context, folderPath)
        generateContractsApi(context, folderPath)
    }

    private fun generateContractsApi(context: HashMap<String, Any>, folderPath: String) {

    }

    private fun setContext(): HashMap<String, Any> {
        return hashMapOf(
            "packageName" to configuration.packageName,
            "ContractConfiguration" to configuration.contracts
        )
    }

    private fun copyGradleFile(folderPath: String) {
        logger.debug("Copying contracts/build.gradle")
        CopyUtils.copyResource(
            "contracts/build.gradle",
            File(folderPath.substringBefore("contracts"))
        )
    }

    private fun copySources(context: HashMap<String, Any>, folderPath: String) {
        File("codegen/src/main/resources/contracts/src/")
            .listFiles()
            ?.forEach { it ->
                logger.debug("Generating from ${it.canonicalPath}")
                TemplateUtils.generateFromTemplate(
                    context = context,
                    outputDir = folderPath,
                    template = TemplateUtils.mustacheTemplate(it.path.substringAfter("resources/")),
                    name = "${it.name.removeSuffix(".mustache")}.kt"
                )
            }
    }
}