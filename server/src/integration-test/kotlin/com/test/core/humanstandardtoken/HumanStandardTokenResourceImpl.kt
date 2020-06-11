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
package com.test.core.humanstandardtoken

import com.test.core.humanstandardtoken.model.AllowanceParameters
import com.test.core.humanstandardtoken.model.ApprovalEventResponse
import com.test.core.humanstandardtoken.model.ApproveAndCallParameters
import com.test.core.humanstandardtoken.model.ApproveParameters
import com.test.core.humanstandardtoken.model.BalanceOfParameters
import com.test.core.humanstandardtoken.model.TransferEventResponse
import com.test.core.humanstandardtoken.model.TransferFromParameters
import com.test.core.humanstandardtoken.model.TransferParameters
import com.test.wrappers.HumanStandardToken
import com.test.wrappers.HumanStandardToken.TRANSFER_EVENT
import mu.KLogging
import org.web3j.openapi.core.models.PrimitivesModel
import org.web3j.openapi.core.models.TransactionReceiptModel
import org.web3j.protocol.core.methods.request.EthFilter
import java.math.BigInteger
import javax.inject.Singleton
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.sse.Sse
import javax.ws.rs.sse.SseEventSink

@Singleton
class HumanStandardTokenResourceImpl(
    private val humanStandardToken: HumanStandardToken
) : HumanStandardTokenResource {

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
    override fun getTransferEvent(transactionReceiptModel: TransactionReceiptModel):
    List<TransferEventResponse> {
        val eventResponse = humanStandardToken.getTransferEvents(
            transactionReceiptModel.toTransactionReceipt()
        )
        return eventResponse.map { TransferEventResponse(it._from, it._to, it._value) }
    }

    override fun getApprovalEvent(transactionReceiptModel: TransactionReceiptModel):
            List<ApprovalEventResponse> {
        val eventResponse = humanStandardToken.getApprovalEvents(
            transactionReceiptModel.toTransactionReceipt()
        )
        return eventResponse.map { ApprovalEventResponse(it._owner, it._spender, it._value) }
    }

    @GET
    @Path("TransferEvent")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun onTransferEvent(@Context sseEventSink: SseEventSink, @Context sse: Sse) {
        humanStandardToken.transferEventFlowable(EthFilter()).doOnNext { event ->
            logger.debug { "${TRANSFER_EVENT.name} received: $event" }
            sseEventSink.send(
                sse.newEventBuilder()
                    .name(TRANSFER_EVENT.name)
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .data(TransferEventResponse::class.java, event)
                    .reconnectDelay(4000)
                    .build()
            )
        }.doOnCancel {
            sseEventSink.close()
            logger.warn { "${TRANSFER_EVENT.name} cancelled" }
        }
    }

    companion object : KLogging()
}
