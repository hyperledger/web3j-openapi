package org.web3j.server

import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.glassfish.jersey.server.ResourceConfig

class Web3jResourceConfig : ResourceConfig() {

    private val mapper = jacksonObjectMapper()
        .setDefaultSetterInfo(JsonSetter.Value.forContentNulls(Nulls.AS_EMPTY))
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        .enable(SerializationFeature.INDENT_OUTPUT)

    init {
        register(JsonMappingExceptionMapper::class.java)
        register(IllegalArgumentExceptionMapper::class.java)
        register(JacksonJaxbJsonProvider(mapper, arrayOf(Annotations.JACKSON)))
    }
}
