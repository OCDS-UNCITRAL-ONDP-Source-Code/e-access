package com.procurement.access.model.dto.lots

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.access.model.dto.ocds.Value

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ItemDto @JsonCreator constructor(
        val id: String?
)