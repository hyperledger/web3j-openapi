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
import com.test.core.humanstandardtoken.model.ApproveAndCallParameters
import com.test.core.humanstandardtoken.model.ApproveParameters
import com.test.core.humanstandardtoken.model.BalanceOfParameters
import com.test.core.humanstandardtoken.model.TransferFromParameters
import com.test.core.humanstandardtoken.model.TransferParameters
import io.swagger.v3.oas.annotations.Operation
import javax.annotation.processing.Generated
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Generated
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
interface HumanStandardTokenResource {

    @get:Path("TransferEvent")
    val transferEvents: TransferEventResource

    @get:Path("ApprovalEvent")
    val approvalEvents: ApprovalEventResource

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("name")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the Name method"
    )
    fun name(): org.web3j.openapi.core.models.PrimitivesModel<kotlin.String>

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("approve")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the Approve method"
    )
    fun approve(approveParameters: ApproveParameters): org.web3j.openapi.core.models.TransactionReceiptModel

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("totalSupply")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the TotalSupply method"
    )
    fun totalSupply(): org.web3j.openapi.core.models.PrimitivesModel<java.math.BigInteger>

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("transferFrom")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the TransferFrom method"
    )
    fun transferFrom(transferFromParameters: TransferFromParameters): org.web3j.openapi.core.models.TransactionReceiptModel

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("decimals")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the Decimals method"
    )
    fun decimals(): org.web3j.openapi.core.models.PrimitivesModel<java.math.BigInteger>

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("version")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the Version method"
    )
    fun version(): org.web3j.openapi.core.models.PrimitivesModel<kotlin.String>

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("balanceOf")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the BalanceOf method"
    )
    fun balanceOf(balanceOfParameters: BalanceOfParameters): org.web3j.openapi.core.models.PrimitivesModel<java.math.BigInteger>

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("symbol")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the Symbol method"
    )
    fun symbol(): org.web3j.openapi.core.models.PrimitivesModel<kotlin.String>

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("transfer")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the Transfer method"
    )
    fun transfer(transferParameters: TransferParameters): org.web3j.openapi.core.models.TransactionReceiptModel

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("approveAndCall")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the ApproveAndCall method"
    )
    fun approveAndCall(approveAndCallParameters: ApproveAndCallParameters): org.web3j.openapi.core.models.TransactionReceiptModel

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("allowance")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Executes the Allowance method"
    )
    fun allowance(allowanceParameters: AllowanceParameters): org.web3j.openapi.core.models.PrimitivesModel<java.math.BigInteger>
}
