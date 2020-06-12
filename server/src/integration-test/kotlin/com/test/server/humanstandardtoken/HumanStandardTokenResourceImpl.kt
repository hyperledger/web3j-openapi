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

import com.test.core.humanstandardtoken.HumanStandardTokenResource
import com.test.core.humanstandardtoken.model.AllowanceParameters
import com.test.core.humanstandardtoken.model.ApproveAndCallParameters
import com.test.core.humanstandardtoken.model.ApproveParameters
import com.test.core.humanstandardtoken.model.BalanceOfParameters
import com.test.core.humanstandardtoken.model.TransferFromParameters
import com.test.core.humanstandardtoken.model.TransferParameters
import com.test.wrappers.HumanStandardToken
import mu.KLogging
import org.web3j.openapi.core.models.PrimitivesModel
import org.web3j.openapi.core.models.TransactionReceiptModel
import java.math.BigInteger
import javax.inject.Singleton

@Singleton // FIXME Why Singleton?
class HumanStandardTokenResourceImpl(
    private val humanStandardToken: HumanStandardToken
) : HumanStandardTokenResource {

    override val transferEvents = TransferEventResourceImpl(humanStandardToken)
    override val approvalEvents = ApprovalEventResourceImpl(humanStandardToken)

    override fun name(): PrimitivesModel<String> =
        org.web3j.openapi.core.models.PrimitivesModel<kotlin.String>(humanStandardToken.name().send())
    override fun approve(approveParameters: ApproveParameters): TransactionReceiptModel =
        TransactionReceiptModel(
            humanStandardToken.approve(
                approveParameters._spender, approveParameters._value
            ).send()
        )
    override fun totalSupply(): PrimitivesModel<BigInteger> =
        org.web3j.openapi.core.models.PrimitivesModel<java.math.BigInteger>(humanStandardToken.totalSupply().send())
    override fun transferFrom(transferFromParameters: TransferFromParameters): TransactionReceiptModel =
        TransactionReceiptModel(
            humanStandardToken.transferFrom(
                transferFromParameters._from, transferFromParameters._to, transferFromParameters._value
            ).send()
        )
    override fun decimals(): PrimitivesModel<BigInteger> =
        org.web3j.openapi.core.models.PrimitivesModel<java.math.BigInteger>(humanStandardToken.decimals().send())
    override fun version(): PrimitivesModel<String> =
        org.web3j.openapi.core.models.PrimitivesModel<kotlin.String>(humanStandardToken.version().send())
    override fun balanceOf(balanceOfParameters: BalanceOfParameters): PrimitivesModel<BigInteger> =
        org.web3j.openapi.core.models.PrimitivesModel<java.math.BigInteger>(
            humanStandardToken.balanceOf(
                balanceOfParameters._owner
            ).send()
        )
    override fun symbol(): PrimitivesModel<String> =
        org.web3j.openapi.core.models.PrimitivesModel<kotlin.String>(humanStandardToken.symbol().send())
    override fun transfer(transferParameters: TransferParameters): TransactionReceiptModel =
        TransactionReceiptModel(
            humanStandardToken.transfer(
                transferParameters._to, transferParameters._value
            ).send()
        )
    override fun approveAndCall(approveAndCallParameters: ApproveAndCallParameters):
    TransactionReceiptModel = TransactionReceiptModel(
        humanStandardToken.approveAndCall(

            approveAndCallParameters._spender, approveAndCallParameters._value, approveAndCallParameters._extraData
        ).send()
    )
    override fun allowance(allowanceParameters: AllowanceParameters): PrimitivesModel<BigInteger> =
        org.web3j.openapi.core.models.PrimitivesModel<java.math.BigInteger>(
            humanStandardToken.allowance(
                allowanceParameters._owner, allowanceParameters._spender
            ).send()
        )

    companion object : KLogging()
}
