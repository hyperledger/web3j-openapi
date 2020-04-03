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
package com.helloworld.server

import com.helloworld.api.GreeterResource
import com.helloworld.api.model.GreeterDeployParameters
import com.helloworld.api.model.NewGreetingParameters
import mu.KLogging
import org.web3j.crypto.Credentials
import org.web3j.greeter.Greeter
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider

class GreeterResourceImpl(
    private val web3j: Web3j,
    private val credentials: Credentials,
    private val transactionManager: TransactionManager,
    private val defaultGasProvider: ContractGasProvider
) : GreeterResource {

    override fun deploy(greetingParameters: GreeterDeployParameters): TransactionReceipt {
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
