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

import jakarta.ws.rs.GET
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

/**
 * A Web3j OpenAPI can contain nested resources.
 *
 * Subclasses may define additional values as JAX-RS sub-resources.
 */
interface SubResource {

    /**
     * Lists all available sub-resources.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun findAll(): List<String>
}
