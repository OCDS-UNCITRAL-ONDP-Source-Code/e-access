package com.procurement.access.infrastructure.bind.quantity

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.access.infrastructure.exception.QuantityValueException
import java.io.IOException
import java.math.BigDecimal

class QuantityDeserializer : JsonDeserializer<BigDecimal>() {
    companion object {
        fun deserialize(text: String): BigDecimal = try {
            BigDecimal(text)
        } catch (exception: Exception) {
            throw QuantityValueException(quantity = text, description = exception.message ?: "")
        }
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): BigDecimal {
        if (jsonParser.currentToken == JsonToken.VALUE_NUMBER_FLOAT) {
            throw QuantityValueException(
                quantity = "\"${jsonParser.text}\"",
                description = "The value must be a real number."
            )
        }
        return deserialize(jsonParser.text)
    }
}
