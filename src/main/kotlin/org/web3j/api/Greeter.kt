package org.web3j.api

import org.web3j.protocol.core.methods.response.TransactionReceipt
import javax.ws.rs.*

@Path("/api/contracts")
interface Greeter {

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
    @Consumes("application/json")
    fun newGreeting(
        @PathParam("contractAddress") contractAddress: String,
        @QueryParam("greeting") greeting: String): TransactionReceipt

    @GET
    @Path("{contractAddress}/greet")
    @Produces("application/json")
    fun greet(@PathParam("contractAddress") contractAddress: String): String

}