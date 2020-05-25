/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j.openapi.server
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.Loader.getResource
import org.eclipse.jetty.util.resource.Resource
import org.glassfish.jersey.servlet.ServletContainer
import org.web3j.openapi.core.spi.OpenApiResourceProvider
import org.web3j.openapi.server.cli.ConfigCLI
import org.web3j.openapi.server.config.OpenApiResourceConfig
import org.web3j.openapi.server.config.OpenApiServerConfig
import java.net.URI
import java.util.ServiceLoader

class OpenApiServer(private val serverConfig: OpenApiServerConfig) : Server() {
    init {
        addConnector(ServerConnector(this).apply {
            host = serverConfig.host
            port = serverConfig.port
        })
        handler = ServletContextHandler(NO_SESSIONS)
        configureSwaggerUi()
        configureOpenApi()
    }

    private fun configureOpenApi() {
        val resourceConfig = OpenApiResourceConfig(serverConfig).apply {
            registerClasses(OpenApiResource::class.java)
        }
        ServiceLoader.load(OpenApiResourceProvider::class.java).forEach {
            resourceConfig.register(it.get())
        }
        val servletHolder = ServletHolder(ServletContainer(resourceConfig))
        (handler as ServletContextHandler).apply {
            addServlet(servletHolder, "/*")
            contextPath = "/"
        }
    }

    private fun configureSwaggerUi() {
        getResource("static/swagger-ui/index.html")?.also {
            val swaggerResourceUri = URI.create(it.toURI().toASCIIString().substringBefore("swagger-ui/"))
            val swaggerServletHolder = ServletHolder("default", DefaultServlet::class.java).apply {
                setInitParameter("dirAllowed", "true")
            }
            (handler as ServletContextHandler).apply {
                baseResource = Resource.newResource(swaggerResourceUri)
                addServlet(swaggerServletHolder, "/swagger-ui/*")
            }
        }
    }
}

fun main(args: Array<String>) {
    ConfigCLI().parse(*args)
}
