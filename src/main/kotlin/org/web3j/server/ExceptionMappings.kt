package org.web3j.server

import com.fasterxml.jackson.databind.JsonMappingException
import org.eclipse.jetty.http.HttpStatus
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.Context
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status
import javax.ws.rs.core.Response.Status.Family
import javax.ws.rs.core.UriInfo
import javax.ws.rs.ext.ExceptionMapper

sealed class BaseExceptionMapper<E : Throwable>(
    private val status: Response.StatusType
) : ExceptionMapper<E> {

    @Context
    private lateinit var uriInfo: UriInfo

    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: E): Response {

        val error = ErrorResponse(
            title = exception.message ?: status.reasonPhrase,
            userAgent = request.getHeader(HttpHeaders.USER_AGENT),
            responseStatus = status.statusCode,
            requestMethod = request.method,
            requestUrl = uriInfo.requestUri.toString()
        )

        return Response.status(status.statusCode).entity(error).build()
    }
}

class JsonMappingExceptionMapper : BaseExceptionMapper<JsonMappingException>(Status.BAD_REQUEST)
class IllegalArgumentExceptionMapper : BaseExceptionMapper<IllegalArgumentException>(CustomStatus.UNPROCESSABLE_ENTITY)

enum class CustomStatus(
    private val _statusCode: Int,
    private val _reasonPhrase: String
) : Response.StatusType {

    UNPROCESSABLE_ENTITY(
        HttpStatus.Code.UNPROCESSABLE_ENTITY.code,
        HttpStatus.Code.UNPROCESSABLE_ENTITY.message
    );

    override fun getStatusCode(): Int = _statusCode
    override fun getFamily(): Family = Family.familyOf(_statusCode)
    override fun getReasonPhrase(): String = _reasonPhrase
}
