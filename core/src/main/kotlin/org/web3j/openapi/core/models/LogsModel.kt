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
import java.math.BigInteger

class LogsModel() {

    constructor(
        _removed: Boolean,
        _logIndex: BigInteger,
        _transactionIndex: BigInteger,
        _transactionHash: String?,
        _blockHash: String?,
        _blockNumber: BigInteger,
        _address: String?,
        _data: String?,
        _type: String?,
        _topics: List<String>?
    ) : this() {
        removed = _removed
        logIndex = _logIndex
        transactionHash = _transactionHash
        transactionIndex = _transactionIndex
        blockHash = _blockHash
        blockNumber = _blockNumber
        address = _address
        data = _data
        type = _type
        topics = _topics
    }

    var removed: Boolean = false
    lateinit var logIndex: BigInteger
    lateinit var transactionIndex: BigInteger
    var transactionHash: String? = null
    var blockHash: String? = null
    lateinit var blockNumber: BigInteger
    var address: String? = null
    var data: String? = null
    var type: String? = null
    var topics: List<String>? = null

    companion object {
        fun fromLogs(logs: List<Log>): List<LogsModel> {
            return logs.map { log ->
                LogsModel(
                    log.isRemoved,
                    log.logIndex,
                    log.transactionIndex,
                    log.transactionHash,
                    log.blockHash,
                    log.blockNumber,
                    log.address,
                    log.data,
                    log.type,
                    log.topics
                )
            }
        }

        fun toLogs(logsModel: List<LogsModel>?): List<Log>? {
            return logsModel?.map { logModel ->
                Log(
                    logModel.removed,
                    logModel.logIndex.toString(16),
                    logModel.transactionIndex.toString(16),
                    logModel.transactionHash,
                    logModel.blockHash,
                    logModel.blockNumber.toString(16),
                    logModel.address,
                    logModel.data,
                    logModel.type,
                    logModel.topics
                )
            }
        }
    }
}
