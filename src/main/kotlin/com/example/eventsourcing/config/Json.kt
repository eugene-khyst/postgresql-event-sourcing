package com.example.eventsourcing.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializationFeature
import org.http4k.format.Jackson

object Json {
    val objectMapper: ObjectMapper = Jackson.mapper.also {
        it.findAndRegisterModules()
        it.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
        it.propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
        it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        it.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
}
