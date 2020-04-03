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
package org.web3j.openapi

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Error")
@JsonPropertyOrder(
    "title",
    "responseStatus",
    "requestMethod",
    "requestUrl",
    "user"
)
data class ErrorResponse(
//    @ApiModelProperty(value = "The error title", readOnly = true)
    val title: String? = null,

//    @ApiModelProperty(value = "The HTTP request method")
    val requestMethod: String? = null,

//    @ApiModelProperty(value = "The HTTP request URL, relative to system base URL")
    val requestUrl: String? = null,

//    @ApiModelProperty(value = "The HTTP response status code ")
    val responseStatus: Int? = null,

//    @ApiModelProperty(value = "The UA string if provided with a request")
    val userAgent: String? = null
)
