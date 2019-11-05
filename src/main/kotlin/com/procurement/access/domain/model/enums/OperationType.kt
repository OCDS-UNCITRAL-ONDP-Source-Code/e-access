package com.procurement.access.domain.model.enums

import com.procurement.access.exception.EnumException

enum class OperationType(val value: String) {
    CREATE_CN("createCN"),
    CREATE_PN("createPN"),
    CREATE_PIN("createPIN"),
    UPDATE_CN("updateCN"),
    UPDATE_PN("updatePN"),
    CREATE_CN_ON_PN("createCNonPN"),
    CREATE_CN_ON_PIN("createCNonPIN"),
    CREATE_PIN_ON_PN("createPINonPN"),
    CREATE_NEGOTIATION_CN_ON_PN("createNegotiationCnOnPn");

    companion object {
        private val elements: Map<String, OperationType> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): OperationType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = OperationType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}