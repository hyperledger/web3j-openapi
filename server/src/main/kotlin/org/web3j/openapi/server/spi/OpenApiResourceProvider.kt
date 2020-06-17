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
package org.web3j.openapi.server.spi

import org.web3j.openapi.core.Web3jOpenApi
import java.util.function.Supplier

/**
 * Defines the application implementation class.
 *
 * This class should be used with the Java SPI framework
 * by defining a file named like this class in `META-INF/services`.
 *
 * The file should contain the full class name of the implementor.
 */
interface OpenApiResourceProvider : Supplier<Class<out Web3jOpenApi>>
