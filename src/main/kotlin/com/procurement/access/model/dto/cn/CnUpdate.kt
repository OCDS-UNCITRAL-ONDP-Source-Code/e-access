package com.procurement.access.model.dto.cn

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.access.databinding.QuantityDeserializer
import com.procurement.access.model.dto.databinding.BooleansDeserializer
import com.procurement.access.model.dto.ocds.*
import com.procurement.access.model.dto.ocds.Unit
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class CnUpdate @JsonCreator constructor(

        @field:Valid @field:NotNull
        val planning: PlanningCnUpdate,

        @field:Valid @field:NotNull
        val tender: TenderCnUpdate
)


data class PlanningCnUpdate @JsonCreator constructor(

        @field:Valid @field:NotNull
        val budget: BudgetCnUpdate,

        val rationale: String?
)

data class BudgetCnUpdate @JsonCreator constructor(

        val description: String?,

        @field:Valid @field:NotNull
        val amount: Value,

        @field:NotNull
        @field:JsonDeserialize(using = BooleansDeserializer::class)
        @get:JsonProperty("isEuropeanUnionFunded")
        val isEuropeanUnionFunded: Boolean,

        @field:Valid @field:NotEmpty
        val budgetBreakdown: List<BudgetBreakdown>
)

data class TenderCnUpdate @JsonCreator constructor(

        @field:NotNull
        val title: String,

        @field:NotNull
        val description: String,

        @field:Valid
        val classification: Classification?,

        @field:NotNull
        val mainProcurementCategory: MainProcurementCategory,

        @field:NotNull
        val procurementMethodDetails: String,

        val procurementMethodRationale: String?,

        val procurementMethodAdditionalInfo: String?,

        @field:NotNull
        val submissionMethodRationale: List<String>,

        @field:NotNull
        val submissionMethodDetails: String,

        @field:NotNull
        val eligibilityCriteria: String,

        @field:NotNull
        val legalBasis: LegalBasis,

        @field:Valid @field:NotNull
        val tenderPeriod: PeriodCnUpdate,

        @field:Valid @field:NotNull
        val procuringEntity: OrganizationReference,

        @field:Valid @field:NotEmpty
        val lots: List<LotCnUpdate>,

        @field:Valid @field:NotEmpty
        val items: List<ItemCnUpdate>,

        @field:Valid @field:NotEmpty
        val documents: List<Document>
)

data class LotCnUpdate @JsonCreator constructor(

        @field:NotNull
        var id: String,

        @field:NotNull
        val title: String,

        @field:NotNull
        val description: String,

        @field:Valid @field:NotNull
        val value: Value,

        @field:Valid @field:NotNull
        val contractPeriod: Period,

        @field:Valid @field:NotNull
        val placeOfPerformance: PlaceOfPerformance
)

data class ItemCnUpdate @JsonCreator constructor(

        @field:NotNull
        var id: String,

        val description: String?,

        @field:Valid @field:NotNull
        val classification: Classification,

        @field:Valid
        val additionalClassifications: HashSet<Classification>?,

        @field:NotNull
        @field:JsonDeserialize(using = QuantityDeserializer::class)
        val quantity: BigDecimal,

        @field:Valid @field:NotNull
        val unit: Unit,

        @field:NotNull
        var relatedLot: String
)

data class PeriodCnUpdate @JsonCreator constructor(

        @field:NotNull
        val endDate: LocalDateTime
)