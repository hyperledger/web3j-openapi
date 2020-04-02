package org.web3j.server

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.server.ServerProperties
import org.glassfish.jersey.servlet.ServletContainer
import org.web3j.something.GreeterResourceImpl
import kotlin.system.exitProcess

fun main() {
    val resourceConfig = Web3jResourceConfig().apply {
        // FIXME Load contract resource classes from eg. command line
        registerClasses(GreeterResourceImpl::class.java)
    }

    val servletHolder = ServletHolder(ServletContainer(resourceConfig)).apply {
        setInitParameter(ServerProperties.PROVIDER_PACKAGES, "org.web3j.something, org.web3j.server")
        initOrder = 0
    }

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
}
