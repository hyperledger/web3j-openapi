package org.web3j.api

import org.web3j.api.model.GreetingParameters
import org.web3j.protocol.core.methods.response.TransactionReceipt
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/api/contracts")
interface GreeterResource {

//    @get:Path("contracts")
//    val deployments: List<Deployment>
//
//    interface Deployment {
//        @POST
//        @Produces("application/json")
//        @Consumes("application/json")
//        fun deploy(greeting: String): String
//    }

    @POST
    @Path("{contractAddress}/newGreeting")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun newGreeting(
        @PathParam("contractAddress") contractAddress: String,
        greetingParameters: GreetingParameters
    ): TransactionReceipt

    @GET
    @Path("{contractAddress}/greet")
    @Produces(MediaType.TEXT_PLAIN)
    fun greet(@PathParam("contractAddress") contractAddress: String): String

}
