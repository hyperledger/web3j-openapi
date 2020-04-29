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
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.servlet.ServletContainer
import org.web3j.openapi.core.spi.OpenApiResourceProvider
import java.util.ServiceLoader
import java.util.concurrent.Callable
import javax.servlet.ServletConfig
import javax.ws.rs.core.Context
import kotlin.system.exitProcess

@Context
lateinit var servletConfig: ServletConfig

open class OpenApiServer: Callable<Int> {

    lateinit var projectName: String
    lateinit var nodeEndpoint: String
    lateinit var privateKey: String

    override fun call(): Int {

        // FIXME Load contract resources, etc. from args
        val resourceConfig = Config(
            projectName,
            nodeEndpoint,
            privateKey
        ).apply {
            registerClasses(OpenApiResource::class.java)
        }

        ServiceLoader.load(OpenApiResourceProvider::class.java).forEach {
            resourceConfig.register(it.get())
        }

        val servletHolder = ServletHolder(ServletContainer(resourceConfig))

        val servletContextHandler = ServletContextHandler(ServletContextHandler.NO_SESSIONS).apply {
            addServlet(servletHolder, "/*")
            contextPath = "/*"
        }

        val server = Server(8080).apply {
            handler = servletContextHandler
        }

        try {
            server.start()
            server.join()
        } catch (ex: Exception) {
            exitProcess(1)
        } finally {
            server.destroy()
        }

        exitProcess(0)
    }

}
