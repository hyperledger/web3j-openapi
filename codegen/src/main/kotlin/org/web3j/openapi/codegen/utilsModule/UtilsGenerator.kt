package org.web3j.openapi.codegen.utilsModule

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.web3j.openapi.codegen.DefaultGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File

class UtilsGenerator(
    private val configuration: GeneratorConfiguration
) : DefaultGenerator {
    private val logger: Logger = LoggerFactory.getLogger(UtilsGenerator::class.java)

    override fun generate() {
        val packageDir = configuration.packageName.split(".").joinToString("/")
        val folderPath = CopyUtils.createTree("utils", packageDir, configuration.outputDir)
        copyGradleFile(folderPath)
        val context = setContext()
        copySources(context, folderPath)
    }

    private fun setContext(): HashMap<String, Any> {
        return hashMapOf("packageName" to configuration.packageName)
    }

    private fun copyGradleFile(folderPath: String) {
        logger.debug("Copying utils/build.gradle")
        CopyUtils.copyResource(
            "utils/build.gradle",
            File(folderPath.substringBefore("utils"))
        )
    }

    private fun copySources(context: HashMap<String, Any>, folderPath: String) {
        File("codegen/src/main/resources/utils/src/")
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