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
package org.web3j.openapi.core

import org.web3j.openapi.core.models.TransactionReceiptModel
import java.util.concurrent.CompletableFuture

/**
 * Generic resource for contract events.
 *
 * @param T the event return type.
 */
interface EventResource<T> {

    /**
     * Contract event client-side subscription.
     * 
     * This method does not add any endpoint to the server OpenAPI,
     * it's implemented dynamically by a client invocation handler.
     *
     * @param onEvent the event consumer function.
     * @return the current state of the subscription. 
     */
    fun onEvent(onEvent: (T) -> Unit): CompletableFuture<Void>

    /**
     * Retrieve events by a transaction receipt model.
     */
    fun findBy(transactionReceiptModel: TransactionReceiptModel): List<T>
}
