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
package org.web3j.server

import mu.KLogging
import org.web3j.api.model.GreeterDeployParameters
import org.web3j.api.model.NewGreetingParameters
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

    override fun deploy(greeterDeployParameters: GreeterDeployParameters): TransactionReceipt {
        val greeter = Greeter.deploy(
            web3j,
            transactionManager,
            defaultGasProvider,
            greeterDeployParameters.greeting
        ).send()

        return greeter.transactionReceipt.get()
    }

    override fun newGreeting(
        contractAddress: String,
        newGreetingParameters: NewGreetingParameters
    ): TransactionReceipt {
        val greeter = Greeter.load(contractAddress, web3j, credentials, defaultGasProvider)
        return greeter.newGreeting(newGreetingParameters.greeting).send()
    }

    override fun greet(contractAddress: String): String {
        val greeter = Greeter.load(contractAddress, web3j, credentials, defaultGasProvider)
        return greeter.greet().send()
    }

    companion object : KLogging()
}
