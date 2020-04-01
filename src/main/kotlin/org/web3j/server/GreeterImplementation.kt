package org.web3j.server

import org.web3j.abi.datatypes.Address
import org.web3j.api.Greeter
import org.web3j.crypto.Credentials
import org.web3j.evm.Configuration
import org.web3j.evm.EmbeddedWeb3jService
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam

class GreeterImplementation: Greeter {

    private val credentials = Credentials
        .create("0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63")

    private val web3j: Web3j

    private val transactionManager: TransactionManager

    private val defaultGasProvider = DefaultGasProvider()


    init {
        web3j = Web3j.build(EmbeddedWeb3jService(Configuration(Address(credentials.address), 10)))

        transactionManager = RawTransactionManager(web3j, credentials)

        val greeter = org.web3j.greeter.Greeter.deploy(web3j, transactionManager, defaultGasProvider, "Hello JAX-RS").send()

        println(greeter.contractAddress)
    }

    override fun newGreeting(@PathParam(value = "contractAddress") contractAddress: String, @QueryParam(value = "greeting") greeting: String): TransactionReceipt {
        val greeter = org.web3j.greeter.Greeter.load(contractAddress, web3j, credentials, defaultGasProvider)
        return greeter.newGreeting(greeting).send()
    }

    override fun greet(@PathParam(value = "contractAddress") contractAddress: String): String {
        val greeter = org.web3j.greeter.Greeter.load(contractAddress, web3j, credentials, defaultGasProvider)
        return greeter.greet().send()
    }
}