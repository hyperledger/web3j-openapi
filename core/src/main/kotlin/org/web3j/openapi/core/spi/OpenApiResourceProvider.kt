package org.web3j.openapi.core.spi

import org.web3j.openapi.core.Web3jOpenApi
import java.util.function.Supplier

interface OpenApiResourceProvider: Supplier<Class<out Web3jOpenApi>>
