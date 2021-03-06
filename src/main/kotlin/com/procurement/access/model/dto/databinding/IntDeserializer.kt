package com.procurement.access.model.dto.databinding

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.access.exception.ErrorException
import com.procurement.access.exception.ErrorType
import java.io.IOException


class IntDeserializer : JsonDeserializer<Int>() {

    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Int {
        if (jsonParser.currentToken != JsonToken.VALUE_NUMBER_INT) {
            throw ErrorException(ErrorType.INVALID_JSON_TYPE, jsonParser.currentName)
        }
        return jsonParser.valueAsInt
    }
}