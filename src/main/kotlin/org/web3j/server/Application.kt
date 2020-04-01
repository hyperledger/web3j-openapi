package org.web3j.server

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.glassfish.jersey.servlet.ServletContainer
import org.web3j.abi.datatypes.Address
import org.web3j.crypto.Credentials
import org.web3j.evm.Configuration
import org.web3j.evm.EmbeddedWeb3jService
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import kotlin.system.exitProcess

fun main() {


    val server = Server(8080)

    val servletContextHandler = ServletContextHandler(ServletContextHandler.NO_SESSIONS)

    servletContextHandler.contextPath = "/*"
    server.handler = servletContextHandler

    val servletHolder = servletContextHandler.addServlet(ServletContainer::class.java, "/*")
    servletHolder.initOrder = 0
    servletHolder.setInitParameter(
        "jersey.config.server.provider.packages",
        "org.web3j.something"
    )

    try {
        server.start()
        server.join()
    } catch (ex: Exception) {
        exitProcess(1)
    } finally {
        server.destroy()
    }
}