package com.procurement.access.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.access.model.dto.databinding.BooleansDeserializer
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Budget @JsonCreator constructor(

        val id: String?,

        val description: String?,

        @field:Valid @field:NotNull
        val amount: Value,

        val project: String?,

        val projectID: String?,

        val uri: String?,

        val source: String?,

        @field:Valid
        val europeanUnionFunding: EuropeanUnionFunding?,

        @field:NotNull
        @field:JsonDeserialize(using = BooleansDeserializer::class)
        @get:JsonProperty("isEuropeanUnionFunded")
        val isEuropeanUnionFunded: Boolean,

        @field:Valid @field:NotEmpty
        val budgetBreakdown: List<BudgetBreakdown>
)