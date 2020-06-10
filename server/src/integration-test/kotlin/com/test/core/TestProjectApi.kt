package com.test.core

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.tags.Tag
import javax.annotation.processing.Generated
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import org.web3j.openapi.core.Web3jOpenApi

@Path("/test")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(
    info = Info(
        title = "Test Web3j OpenApi",
        contact = Contact(
            name = "Web3 Labs",
            email = "hi@web3labs.com",
            url = "http://web3labs.com"
        )
    ),
    tags = [
        Tag(name = "default", description = "List existing contracts names"),
        Tag(name = "HumanStandardToken", description = "List HumanStandardToken method&#39;s calls")
    ]
)
@Generated
interface TestProjectApi : Web3jOpenApi {

    @get:Path("contracts")
    override val contracts: TestProjectResource
}
