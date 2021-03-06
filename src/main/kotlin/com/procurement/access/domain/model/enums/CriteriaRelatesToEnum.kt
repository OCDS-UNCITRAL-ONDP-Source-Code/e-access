package com.procurement.access.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.access.domain.EnumElementProvider

enum class CriteriaRelatesToEnum(@JsonValue override val key: String) : EnumElementProvider.Key {
    AWARD("award"),
    ITEM("item"),
    LOT("lot"),
    TENDERER("tenderer"),
    QUALIFICATION("qualification");

    override fun toString(): String = key

    companion object : EnumElementProvider<CriteriaRelatesToEnum>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CriteriaRelatesToEnum.orThrow(name)
    }
}
