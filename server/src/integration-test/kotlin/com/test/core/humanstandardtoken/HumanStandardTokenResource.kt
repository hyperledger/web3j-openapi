package com.test.core.humanstandardtoken

import com.test.core.humanstandardtoken.model.AllowanceParameters
import com.test.core.humanstandardtoken.model.ApprovalEventResponse
import com.test.core.humanstandardtoken.model.ApproveAndCallParameters
import com.test.core.humanstandardtoken.model.ApproveParameters
import com.test.core.humanstandardtoken.model.BalanceOfParameters
import com.test.core.humanstandardtoken.model.TransferEventResponse
import com.test.core.humanstandardtoken.model.TransferFromParameters
import com.test.core.humanstandardtoken.model.TransferParameters
import io.swagger.v3.oas.annotations.Operation
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
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

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("TransferEvent")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Get the Transfer event"
    )
    fun getTransferEvent(transactionReceiptModel: org.web3j.openapi.core.models.TransactionReceiptModel): List<TransferEventResponse>

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ApprovalEvent")
    @Operation(
        tags = ["HumanStandardToken"],
        summary = "Get the Approval event"
    )
    fun getApprovalEvent(transactionReceiptModel: org.web3j.openapi.core.models.TransactionReceiptModel): List<ApprovalEventResponse>

//    /**
//     * This method will not add endpoints.
//     */
//    fun onTransferEvent(eventConsumer: Consumer<TransferEventResponse>): CompletableFuture<Void>
}
