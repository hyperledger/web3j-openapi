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

import com.test.core.humanstandardtoken.model.HumanStandardTokenDeployParameters
import io.swagger.v3.oas.annotations.Operation
import javax.annotation.processing.Generated
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import org.web3j.openapi.core.CONTRACT_ADDRESS
import org.web3j.openapi.core.CONTRACT_ADDRESS_PATH
import org.web3j.protocol.core.methods.response.TransactionReceipt

@Generated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface HumanStandardTokenLifecycle {

    @POST
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Deploys the HumanStandardToken contract"
    )
    fun deploy(parameters: HumanStandardTokenDeployParameters): TransactionReceipt

    @Path(CONTRACT_ADDRESS_PATH)
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Loads the HumanStandardToken contract"
    )
    fun load(
        @PathParam(CONTRACT_ADDRESS)
        contractAddress: String
    ): HumanStandardTokenResource
}
