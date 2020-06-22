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
package org.web3j.openapi.server

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.test.core.TestProjectApi
import com.test.core.humanstandardtoken.HumanStandardTokenResource
import com.test.core.humanstandardtoken.model.ApproveParameters
import com.test.core.humanstandardtoken.model.HumanStandardTokenDeployParameters
import org.glassfish.jersey.test.JerseyTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.web3j.EVMTest
import org.web3j.NodeType
import org.web3j.openapi.client.ClientFactory
import org.web3j.openapi.client.ClientService
import org.web3j.openapi.server.config.OpenApiResourceConfig
import org.web3j.openapi.server.config.OpenApiServerConfig
import java.math.BigInteger
import java.net.URL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Classes used in this test will be generated using a gradle task.
 */
@EVMTest(type = NodeType.BESU)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerTest : JerseyTest() {

    private lateinit var contract: HumanStandardTokenResource

    private val client: TestProjectApi by lazy {
        ClientFactory.create(
            TestProjectApi::class.java,
            ClientService(target().uri.toString())
        )
    }

    override fun configure() =
        OpenApiResourceConfig(
            OpenApiServerConfig(
                projectName = "Test",
                nodeEndpoint = URL("http://localhost:8545"),
                privateKey = PRIVATE_KEY,
                host = "localhost",
                port = 0
            )
        )

    @BeforeAll
    override fun setUp() {
        super.setUp()
        contract = client.contracts.humanStandardToken.deploy(
            HumanStandardTokenDeployParameters(
                BigInteger.TEN, "Test", BigInteger.ZERO, "TEST"
            )
        ).let {
            client.contracts.humanStandardToken.load(it.contractAddress)
        }
    }

    @AfterAll
    override fun tearDown() = super.tearDown()

    @Test
    fun `find all available contract paths`() {
        assertThat(client.contracts.findAll()).containsExactly("humanstandardtoken")
    }

    @Test
    fun `invoke contract function`() {
        assertThat(contract.approve(ApproveParameters(ADDRESS, BigInteger.TEN))).isNotNull()
        assertThat(contract.decimals().result).isEqualTo(BigInteger.ZERO)
        assertThat(contract.symbol().result).isEqualTo("TEST")
    }

    @Test
    fun `on contract event`() {
        val countDownLatch = CountDownLatch(1)
        contract.approvalEvents.onEvent { countDownLatch.countDown() }
        contract.approve(ApproveParameters(ADDRESS, BigInteger.TEN))
        assertThat(countDownLatch.await(10, TimeUnit.SECONDS)).isTrue()
    }

    companion object {
        private const val ADDRESS = "fe3b557e8fb62b89f4916b721be55ceb828dbd73"
        private const val PRIVATE_KEY = "8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63"
    }
}
