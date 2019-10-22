package com.procurement.access.infrastructure.bind.coefficient.value

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.access.domain.model.coefficient.CoefficientValue
import com.procurement.access.infrastructure.exception.CoefficientValueException
import java.io.IOException
import java.math.BigDecimal

class CoefficientValueDeserializer : JsonDeserializer<CoefficientValue>() {
    companion object {
        fun deserialize(value: String): CoefficientValue = CoefficientValue.AsString(value)
        fun deserialize(value: Boolean): CoefficientValue = CoefficientValue.AsBoolean(value)
        fun deserialize(value: BigDecimal): CoefficientValue = CoefficientValue.AsNumber(value)
        fun deserialize(value: Int): CoefficientValue = CoefficientValue.AsInteger(value)
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): CoefficientValue {
        return when (jsonParser.currentToken) {
            JsonToken.VALUE_STRING -> deserialize(jsonParser.text)
            JsonToken.VALUE_FALSE -> deserialize(false)
            JsonToken.VALUE_TRUE -> deserialize(true)
            JsonToken.VALUE_NUMBER_INT -> deserialize(jsonParser.intValue.toBigDecimal())
            JsonToken.VALUE_NUMBER_FLOAT -> deserialize(jsonParser.decimalValue)
            else -> throw CoefficientValueException(coefficientValue = jsonParser.text, description = "Incorrect type")
        }
    }
}