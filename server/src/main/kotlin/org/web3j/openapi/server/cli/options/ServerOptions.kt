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
package org.web3j.openapi.server.cli.options

import picocli.CommandLine.Option
import java.net.InetAddress

class ServerOptions {

    @Option(
        names = ["--host"],
        description = ["specify the host name"],
        defaultValue = "localhost"
    )
    lateinit var host: InetAddress

    @Option(
        names = ["--port"],
        description = ["specify the port number"],
        defaultValue = "8080"
    )
    var port: Int = 8080
}
