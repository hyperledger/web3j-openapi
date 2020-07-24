package org.web3j.openapi.core

import io.swagger.v3.oas.annotations.Operation
import org.web3j.openapi.core.CONTRACT_ADDRESS
import org.web3j.openapi.core.CONTRACT_ADDRESS_PATH
import javax.ws.rs.Path
import javax.ws.rs.PathParam

interface ContractLifecycle {
	@Path(CONTRACT_ADDRESS_PATH)
	fun load(
			@PathParam(CONTRACT_ADDRESS)
			contractAddress: String
	): ContractResource

	@Operation(summary = "Loads the contract from a predefined address")
	fun load(): ContractResource
}
