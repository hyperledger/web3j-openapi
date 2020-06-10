package com.test.server

import com.test.core.humanstandardtoken.HumanStandardTokenLifecycleImpl
import com.test.core.TestProjectResource
import org.glassfish.jersey.server.ExtendedUriInfo
import org.web3j.openapi.server.ContractResourceImpl
import org.web3j.protocol.Web3j
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import javax.annotation.processing.Generated

@Generated
class TestProjectResourceImpl(
    web3j: Web3j,
    transactionManager: TransactionManager,
    defaultGasProvider: ContractGasProvider,
    uriInfo: ExtendedUriInfo
) : TestProjectResource, ContractResourceImpl(uriInfo) {

    override val humanStandardToken = HumanStandardTokenLifecycleImpl(
        web3j,
        transactionManager,
        defaultGasProvider
    )
}
