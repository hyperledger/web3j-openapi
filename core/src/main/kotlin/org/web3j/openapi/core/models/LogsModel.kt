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

data class LogsModel(
    var removed: Boolean = false,
    var logIndex: BigInteger = BigInteger.ZERO,
    var transactionIndex: BigInteger = BigInteger.ZERO,
    var transactionHash: String? = null,
    var blockHash: String? = null,
    var blockNumber: BigInteger = BigInteger.ZERO,
    var address: String? = null,
    var data: String? = null,
    var type: String? = null,
    var topics: List<String>? = null
) {
    constructor(log: Log) : this (
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
