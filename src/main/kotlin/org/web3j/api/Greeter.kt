package org.web3j.api

import javax.ws.rs.Path
import org.web3j.greeter.Greeter as GreetContract

@Path("/greeter")
interface Greeter {

    companion object : LifeCycle<GreetContract> {
        override fun load(service: ContractService) = ClientBuilder.build(
            GreetContract::class.java, service
        )
    }

}