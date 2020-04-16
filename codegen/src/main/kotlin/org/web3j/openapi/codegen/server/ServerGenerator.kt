package org.web3j.openapi.codegen.server

import org.web3j.openapi.codegen.DefaultGenerator
import org.web3j.openapi.codegen.config.GeneratorConfiguration
import org.web3j.openapi.codegen.utils.CopyUtils
import org.web3j.openapi.codegen.utils.TemplateUtils
import java.io.File

class ServerGenerator (
    override val configuration: GeneratorConfiguration
) : DefaultGenerator(
    configuration
) {
    override val packageDir = configuration.packageName.split(".").joinToString("/")
    override val folderPath = CopyUtils.createTree("server", packageDir, configuration.outputDir)

    override fun generate() {
        copyGradleFile(folderPath)
        val context = setContext()
        copyResources()
        copySources(context)
    }

    private fun setContext(): HashMap<String, Any> {
        return hashMapOf("packageName" to configuration.packageName)
    }

    private fun copyGradleFile(folderPath: String) {
        logger.debug("Copying server/build.gradle")
        CopyUtils.copyResource(
            "server/build.gradle",
            File(folderPath.substringBefore("server"))
        )
    }

    private fun copyResources() {
        File("${folderPath.substringBefore("main")}${File.separator}main${File.separator}resources")
            .apply {
                mkdirs()
            }
        logger.debug("Copying server/resources")
        CopyUtils.copyResource(
            "server/src/main/resources/logback.xml",
            File(folderPath.substringBefore("server"))
        )
        CopyUtils.copyResource(
            "server/src/main/resources/logging.properties",
            File(folderPath.substringBefore("server"))
        )
    }

    private fun copySources(context: HashMap<String, Any>) {
        File("codegen/src/main/resources/server/src/")
            .listFiles()
            .filter { !it.isDirectory }
            .forEach {
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
