package org.web3j.api

import org.web3j.tx.Contract

interface LifeCycle<T: Contract> {

    /**
     * Loads a Contract of type [T] from a node.
     */
    fun load(service: ContractService): T
}
