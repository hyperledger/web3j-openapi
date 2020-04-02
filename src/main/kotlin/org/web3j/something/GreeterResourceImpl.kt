package org.web3j.something

import mu.KLogging
import org.web3j.api.model.GreetingParameters
import org.web3j.crypto.Credentials
import org.web3j.greeter.Greeter
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.DefaultGasProvider

class GreeterResourceImpl : org.web3j.api.GreeterResource {

    private val credentials = Credentials
        .create("0x19FF26B1B1263874C18A1B2AB0DAE3E37BD0944E981B308462FD08824BAA2C63")

    private val web3j: Web3j

    private val transactionManager: TransactionManager

    private val defaultGasProvider = DefaultGasProvider()

    init {
        web3j = Web3j.build(HttpService("https://rinkeby.infura.io/v3/3ab1d29a341d448c8453c5835080dc2a"))

        transactionManager = RawTransactionManager(web3j, credentials)
    }

    override fun deploy(greetingParameters: GreetingParameters): TransactionReceipt {
        val greeter = Greeter.deploy(
            web3j,
            transactionManager,
            defaultGasProvider,
            greetingParameters.greeting
        ).send()

        return greeter.transactionReceipt.get()
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
