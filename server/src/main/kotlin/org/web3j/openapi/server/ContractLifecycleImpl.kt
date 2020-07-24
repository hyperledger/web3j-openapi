package org.web3j.openapi.server

import org.web3j.abi.datatypes.Address
import org.web3j.openapi.core.ContractLifecycle
import org.web3j.openapi.core.ContractResource
import javax.ws.rs.NotFoundException

abstract class ContractLifecycleImpl(
		private val contractAddress : Address?
) : ContractLifecycle {
	override fun load(): ContractResource {
		return contractAddress?.run {
			load(contractAddress.value)
		} ?: throw NotFoundException("Contract address not defined!")
	}
}