package com.test.server

import com.test.core.TestProjectApi
import javax.annotation.processing.Generated
import javax.inject.Inject
import javax.ws.rs.core.Context
import org.glassfish.jersey.server.ExtendedUriInfo
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider

@Generated
class TestProjectApiImpl @Inject constructor(
    web3j: Web3j,
    credentials: Credentials,
    gasProvider: ContractGasProvider,
    @Context uriInfo: ExtendedUriInfo
) : TestProjectApi {

    override val contracts = TestProjectResourceImpl(
        web3j, RawTransactionManager(web3j, credentials), gasProvider, uriInfo
    )
}
