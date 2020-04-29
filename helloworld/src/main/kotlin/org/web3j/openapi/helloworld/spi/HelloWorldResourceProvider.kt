package org.web3j.openapi.helloworld.spi

import org.web3j.openapi.core.spi.OpenApiResourceProvider
import org.web3j.openapi.helloworld.server.HelloWorldApiImpl

class HelloWorldResourceProvider : OpenApiResourceProvider {
    override fun get() = HelloWorldApiImpl::class.java
}
