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

import com.test.core.humanstandardtoken.TransferEventResource
import com.test.core.humanstandardtoken.model.TransferEventResponse
import com.test.wrappers.HumanStandardToken
import org.web3j.openapi.core.models.TransactionReceiptModel
import org.web3j.openapi.server.SseUtils
import org.web3j.protocol.core.methods.request.EthFilter
import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.sse.Sse
import javax.ws.rs.sse.SseEventSink

class TransferEventResourceImpl(
    private val humanStandardToken: HumanStandardToken
) : TransferEventResource {

    override fun findBy(transactionReceiptModel: TransactionReceiptModel):
            List<TransferEventResponse> {
        val eventResponse = humanStandardToken.getTransferEvents(
            transactionReceiptModel.toTransactionReceipt()
        )
        return eventResponse.map { TransferEventResponse(it._from, it._to, it._value) }
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    fun subscribe(@Context eventSink: SseEventSink, @Context sse: Sse) {
        humanStandardToken.transferEventFlowable(EthFilter()).also { flowable ->
            val eventClass = HumanStandardToken.TransferEventResponse::class.java
            SseUtils.subscribe(HumanStandardToken.TRANSFER_EVENT, eventClass, flowable, eventSink, sse) {
                TransferEventResponse(_from = it._from, _to = it._to, _value = it._value)
            }
        }
    }
}
