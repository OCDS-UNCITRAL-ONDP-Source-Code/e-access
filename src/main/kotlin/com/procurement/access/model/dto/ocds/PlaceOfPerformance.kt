package com.procurement.access.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PlaceOfPerformance(

        @JsonProperty("address")
        val address: Address?,

        @JsonProperty("description")
        val description: String?
)