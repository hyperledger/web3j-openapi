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
package org.web3j.openapi.server

import io.reactivex.Flowable
import mu.KLogging
import org.web3j.abi.datatypes.Event
import javax.ws.rs.core.MediaType
import javax.ws.rs.sse.Sse
import javax.ws.rs.sse.SseEventSink

object SseUtils : KLogging() {
    fun <T, R> subscribe(
        eventType: Event,
        eventClass: Class<T>,
        flowable: Flowable<T>,
        eventSink: SseEventSink,
        sse: Sse,
        mapping: (T) -> R,
    ) {
        flowable.blockingSubscribe({ event ->
            logger.debug { "Received ${eventType.name} event." }
            eventSink.send(
                sse.newEventBuilder()
                    .name(eventType.name)
                    .mediaType(MediaType.APPLICATION_JSON_TYPE)
                    .data(eventClass, mapping.invoke(event))
                    .build(),
            )
        }, {
            logger.warn { "Error on ${eventType.name} event sink: ${it.javaClass.canonicalName}" }
        }, {
            logger.warn { "${eventType.name} event sink completed." }
            eventSink.close()
        })
    }
}
