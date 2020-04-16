package org.web3j.openapi.codegen.contracts

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.web3j.openapi.codegen.DefaultGenerator
import org.web3j.openapi.codegen.client.ClientGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File


class ContractsGenerator(
    override val configuration: GeneratorConfiguration
) : DefaultGenerator(
    configuration
) {
    override val folderPath = CopyUtils.createTree("contracts", packageDir, configuration.outputDir)

    override fun generate() {
        configuration.contracts.forEach {
            logger.debug("Creating ${it.contractDetails.capitalizedContractName()} folders")
            File("$folderPath${File.separator}${it.contractDetails.capitalizedContractName()}${File.separator}api${File.separator}model")
                .apply {
                    mkdirs()
                }
            File("$folderPath${File.separator}${it.contractDetails.capitalizedContractName()}${File.separator}server")
                .apply {
                    mkdirs()
                }
        }

        copyGradleFile()
        val context = setContext()
        copySources(context)
        generateContractsApi(context)
    }

    private fun generateContractsApi(context: HashMap<String, Any>) {

    }

    private fun setContext(): HashMap<String, Any> {
        return hashMapOf(
            "packageName" to configuration.packageName,
            "ContractConfiguration" to configuration.contracts
        )
    }

    private fun copyGradleFile() {
        logger.debug("Copying contracts/build.gradle")
        CopyUtils.copyResource(
            "contracts/build.gradle",
            File(folderPath.substringBefore("contracts"))
        )
    }

    private fun copySources(context: HashMap<String, Any>) {
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