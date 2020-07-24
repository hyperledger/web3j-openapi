package org.web3j.openapi.server.config

import org.web3j.abi.datatypes.Address

data class ContractAddresses (
    val addresses: Map<String, Address>?
)