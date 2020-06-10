package com.test.server

import org.web3j.openapi.core.spi.OpenApiResourceProvider

class TestProjectResourceProvider : OpenApiResourceProvider {
    override fun get() = TestProjectApiImpl::class.java
}
