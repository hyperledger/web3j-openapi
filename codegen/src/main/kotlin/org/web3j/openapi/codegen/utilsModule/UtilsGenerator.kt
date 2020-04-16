package org.web3j.openapi.codegen.utilsModule

import org.web3j.openapi.codegen.DefaultGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File

class UtilsGenerator(
    override val configuration: GeneratorConfiguration
) : DefaultGenerator(
    configuration
) {
    override val folderPath = CopyUtils.createTree("utils", packageDir, configuration.outputDir)

    override fun generate() {
        copyGradleFile()
        val context = setContext()
        copySources(context)
    }

    private fun setContext(): HashMap<String, Any> {
        return hashMapOf("packageName" to configuration.packageName)
    }

    private fun copyGradleFile() {
        logger.debug("Copying utils/build.gradle")
        CopyUtils.copyResource(
            "utils/build.gradle",
            File(folderPath.substringBefore("utils"))
        )
    }

    private fun copySources(context: HashMap<String, Any>) {
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