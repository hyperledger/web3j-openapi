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
package com.helloworld.api

import com.helloworld.api.model.GreeterDeployParameters
import com.helloworld.api.model.NewGreetingParameters
import org.web3j.protocol.core.methods.response.TransactionReceipt
import javax.annotation.processing.Generated
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Generated
@Path("Greeter")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface GreeterResource {

    @POST
    fun deploy(greetingParameters: GreeterDeployParameters): TransactionReceipt

    @POST
    @Path("{contractAddress: 0x[a-f0-9]{40}}/newGreeting")
    fun newGreeting(
        @PathParam("contractAddress") contractAddress: String,
        newGreetingParameters: NewGreetingParameters
    ): TransactionReceipt

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{contractAddress: 0x[a-f0-9]{40}}/greet")
    fun greet(@PathParam("contractAddress") contractAddress: String): String
}
