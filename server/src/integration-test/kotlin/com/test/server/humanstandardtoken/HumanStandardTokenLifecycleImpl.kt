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
package com.test.server.humanstandardtoken

import com.test.core.humanstandardtoken.HumanStandardTokenLifecycle
import com.test.core.humanstandardtoken.model.HumanStandardTokenDeployParameters
import com.test.wrappers.HumanStandardToken
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import javax.annotation.processing.Generated
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Generated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class HumanStandardTokenLifecycleImpl(
    private val web3j: Web3j,
    private val transactionManager: TransactionManager,
    private val defaultGasProvider: ContractGasProvider
) : HumanStandardTokenLifecycle {

    override fun deploy(parameters: HumanStandardTokenDeployParameters): TransactionReceipt {
        val humanStandardToken = HumanStandardToken.deploy(
            web3j,
            transactionManager,
            defaultGasProvider, parameters._initialAmount, parameters._tokenName, parameters._decimalUnits, parameters._tokenSymbol
        ).send()

        return humanStandardToken.transactionReceipt.get()
    }

    override fun load(contractAddress: String) =
        HumanStandardTokenResourceImpl(
            HumanStandardToken.load(contractAddress, web3j, transactionManager, defaultGasProvider)
        )
}
