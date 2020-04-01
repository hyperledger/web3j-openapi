package org.web3j.server

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.servlet.ServletContainer

fun main() {
//    val resourceConfig = DefaultResourceConfig(Greeter::class.java)
//    // The following line is to enable GZIP when client accepts it
//    resourceConfig.getContainerResponseFilters().add(GZIPContentEncodingFilter())
//    val server: HttpServer = GrizzlyServerFactory.createHttpServer("http://0.0.0.0:5555", resourceConfig)
//    try {
//        println("Press any key to stop the service...")
//        System.`in`.read()
//    } finally {
//        server.stop()
//    }

    val context = ServletContextHandler(ServletContextHandler.SESSIONS)
    context.setContextPath("/")

    val jettyServer = Server(8080)
    jettyServer.setHandler(context)

    val jerseyServlet: ServletHolder = context.addServlet(
        ServletContainer::class.java, "/*"
    )
    jerseyServlet.setInitOrder(0)

    jerseyServlet.setInitParameter(
        "jersey.config.server.provider.classnames",
        GreeterImplementation::class.java.canonicalName
    )

    try {
        jettyServer.start()
        jettyServer.join()
    } finally {
        jettyServer.destroy()
    }
}