package org.web3j.something

import org.web3j.abi.datatypes.Address
import org.web3j.crypto.Credentials
import org.web3j.evm.Configuration
import org.web3j.evm.EmbeddedWeb3jService
import org.web3j.greeter.Greeter
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.DefaultGasProvider

fun greeter(): Greeter? {
     val credentials = Credentials
        .create("0x8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63")

     val web3j: Web3j = Web3j.build(EmbeddedWeb3jService(Configuration(Address(credentials.address), 10)))

     val transactionManager: TransactionManager = RawTransactionManager(web3j, credentials)

     val defaultGasProvider = DefaultGasProvider()
    return  Greeter.deploy(web3j, transactionManager, defaultGasProvider, "Hello JAX-RS").send()
}