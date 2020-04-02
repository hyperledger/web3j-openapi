package org.web3j.something

import mu.KLogging
import org.web3j.abi.datatypes.Address
import org.web3j.api.model.GreetingParameters
import org.web3j.crypto.Credentials
import org.web3j.evm.Configuration
import org.web3j.evm.EmbeddedWeb3jService
import org.web3j.greeter.Greeter
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("/api/contracts")
class GreeterResourceImpl : org.web3j.api.GreeterResource {

    private val credentials = Credentials
        .create("0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63")

    private val web3j: Web3j

    private val transactionManager: TransactionManager

    private val defaultGasProvider = DefaultGasProvider()

    private val greeter: Greeter

    private val address: String

    init {
        web3j = Web3j.build(EmbeddedWeb3jService(Configuration(Address(credentials.address), 10)))

        transactionManager = RawTransactionManager(web3j, credentials)

        greeter = Greeter.deploy(
            web3j,
            transactionManager,
            defaultGasProvider,
            "Hello JAX-RS"
        ).send()!!

        address = greeter.contractAddress
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun address(): String {
        return address
    }

    override fun newGreeting(
        contractAddress: String,
        greetingParameters: GreetingParameters
    ): TransactionReceipt {
        val greeter = Greeter.load(contractAddress, web3j, credentials, defaultGasProvider)
        return greeter.newGreeting(greetingParameters.greeting).send()
    }

    override fun greet(contractAddress: String): String {
        val greeter = Greeter.load(contractAddress, web3j, credentials, defaultGasProvider)
        return greeter.greet().send()
    }

    companion object : KLogging()
}
