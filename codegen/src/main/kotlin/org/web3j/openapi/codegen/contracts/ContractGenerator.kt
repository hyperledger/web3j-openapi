package org.web3j.openapi.codegen.contracts

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.web3j.openapi.codegen.DefaultGenerator
import org.web3j.openapi.codegen.client.ClientGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File


class ContractGenerator(
    private val configuration: GeneratorConfiguration
) : DefaultGenerator {
    private val logger: Logger = LoggerFactory.getLogger(ClientGenerator::class.java)

    override fun generate() {
        val packageDir = configuration.packageName.split(".").joinToString("/")

        val folderPath = CopyUtils.createTree("contracts", packageDir, configuration.outputDir)
        configuration.contracts.forEach {
            logger.debug("Creating ${it.contractName} folder")
            File("$folderPath${File.separator}${it.contractName}").apply { mkdirs() }
        }

        copyGradleFile(folderPath)
        val context = setContext()
        copySources(context, folderPath)
        generateContractsApi(context, folderPath)
    }

    private fun generateContractsApi(context: HashMap<String, Any>, folderPath: String) {

    }

    private fun setContext(): HashMap<String, Any> {
        return hashMapOf(
            "packageName" to configuration.packageName,
            "ContractConfiguration" to configuration.contracts // FIXME: make the names of the variables in the generated names lowercase
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