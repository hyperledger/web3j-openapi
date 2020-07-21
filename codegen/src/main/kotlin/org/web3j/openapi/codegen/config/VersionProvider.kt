package org.web3j.openapi.codegen.config

import picocli.CommandLine
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Properties

object VersionProvider : CommandLine.IVersionProvider {

    val versionName: String
    val buildTimestamp: OffsetDateTime

    private val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS O")

    init {
        val url = javaClass.classLoader.getResource("openapi-version.properties")
            ?: throw IllegalStateException("No openapi-version.properties file found in the classpath.")

        val properties = Properties().apply { load(url.openStream()) }

        versionName = properties.getProperty("version")
        buildTimestamp = properties.getProperty("timestamp").toLong().let {
            Instant.ofEpochMilli(it).atOffset(ZoneOffset.UTC)
        }
    }

    override fun getVersion(): Array<String> {
        return arrayOf(
            "Version: $versionName",
            "Build timestamp: ${buildTimestamp.let { timeFormatter.format(it) }}"
        )
    }
}
