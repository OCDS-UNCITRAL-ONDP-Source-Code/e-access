package com.procurement.access.application.service

import com.procurement.access.domain.model.requirement.response.RequirementRsValue
import java.time.LocalDateTime

data class CheckResponsesData(
    val items: List<Item>,
    val bid: Bid
) {
    data class Item(
        val id: String
    )

    data class Bid(
        val requirementResponses: List<RequirementResponse>,
        val relatedLots: List<String>
    ) {
        data class RequirementResponse(
            val id: String,
            val title: String,

            val description: String?,

            val value: RequirementRsValue,
            val requirement: Requirement,

            val period: Period?
        ) {
            data class Requirement(
                val id: String
            )

            data class Period(
                val startDate: LocalDateTime,
                val endDate: LocalDateTime
            )
        }
    }
}
