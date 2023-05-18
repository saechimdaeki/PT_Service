package com.example.ptbatch.domain.user

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter

class MetaJsonConverter : AttributeConverter<Map<String, Any>, String> {

    val objectMapper = ObjectMapper()
    override fun convertToDatabaseColumn(attribute: Map<String, Any>): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): Map<String, Any> {
        return objectMapper.readValue(dbData, Map::class.java) as Map<String, Any>
    }
}