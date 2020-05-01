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

import org.aeonbits.owner.Config
import org.aeonbits.owner.Config.LoadPolicy
import org.aeonbits.owner.Config.LoadType
import org.aeonbits.owner.Config.Sources
import org.aeonbits.owner.Config.Key
import org.aeonbits.owner.Config.DefaultValue

@LoadPolicy(LoadType.MERGE)
@Sources(
    "system:env",
    "system:properties",
    "file:/.web3j.openapi.config"
)
interface ServerConfig : Config {
    @Key("projectName")
    @DefaultValue("Generated Web3j-OpenApi project")
    fun projectName(): String

    @Key("WEB3J_OPENAPI_NODE_ENDPOINT")
    fun nodeEndpoint(): String

    @Key("WEB3J_OPENAPI_PRIVATEKEY")
    fun privateKey(): String

    @Key("WEB3J_OPENAPI_SERVER_HOST")
    @DefaultValue("localhost")
    fun host(): String

    @Key("WEB3J_OPENAPI_SERVER_PORT")
    @DefaultValue("8080")
    fun port(): Int
}
