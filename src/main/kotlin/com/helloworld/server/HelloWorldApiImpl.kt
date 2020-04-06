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
package com.helloworld.server

import org.glassfish.jersey.server.ExtendedUriInfo
import org.web3j.crypto.Credentials
import org.web3j.openapi.Web3jOpenApi
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider
import javax.annotation.processing.Generated
import javax.inject.Inject
import javax.ws.rs.core.Context

@Generated
class HelloWorldApiImpl @Inject constructor(
    web3j: Web3j,
    credentials: Credentials,
    defaultGasProvider: ContractGasProvider,
    @Context uriInfo: ExtendedUriInfo
) : Web3jOpenApi {

    override val contracts = HelloWorldContractResourceImpl(
        web3j, RawTransactionManager(web3j, credentials), defaultGasProvider, uriInfo
    )
}
