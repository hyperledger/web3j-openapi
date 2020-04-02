package org.web3j.server

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
