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
package org.web3j.openapi.core.models

import org.web3j.protocol.core.methods.response.Log
import org.web3j.protocol.core.methods.response.TransactionReceipt
import java.math.BigInteger

data class TransactionReceiptModel(
    val transactionHash: String,
    val transactionIndex: BigInteger,
    val blockHash: String,
    val blockNumber: BigInteger,
    val cumulativeGasUsed: BigInteger,
    val gasUsed: BigInteger,
    val contractAddress: String?,
    val root: String?,
    val status: String,
    val from: String?,
    val to: String?,
    val logs: List<LogsModel>?,
    val logsBloom: String?,
    val revertReason: String?,
    val type: String,
    val effectiveGasPrice: String,
) {
    constructor(txReceipt: TransactionReceipt) : this(
        txReceipt.transactionHash,
        txReceipt.transactionIndex,
        txReceipt.blockHash,
        txReceipt.blockNumber,
        txReceipt.cumulativeGasUsed,
        txReceipt.gasUsed,
        txReceipt.contractAddress,
        txReceipt.root,
        txReceipt.status,
        txReceipt.from,
        txReceipt.to,
        txReceipt.logs.map { LogsModel(it) },
        txReceipt.logsBloom,
        txReceipt.revertReason,
        txReceipt.type,
        txReceipt.effectiveGasPrice,
    ) {
    }

    fun toTransactionReceipt(): TransactionReceipt {
        return TransactionReceipt(
            this.transactionHash,
            this.transactionIndex.toString(16),
            this.blockHash,
            this.blockNumber.toString(16),
            this.cumulativeGasUsed.toString(16),
            this.gasUsed.toString(16),
            this.contractAddress,
            this.root,
            this.status,
            this.from,
            this.to,
            this.logs?.map {
                Log(
                    it.removed,
                    it.logIndex.toString(16),
                    it.transactionIndex.toString(16),
                    it.transactionHash,
                    it.blockHash,
                    it.blockNumber.toString(16),
                    it.address,
                    it.data,
                    it.type,
                    it.topics,
                )
            },
            this.logsBloom,
            this.revertReason,
            this.type,
            this.effectiveGasPrice,
        )
    }
}
