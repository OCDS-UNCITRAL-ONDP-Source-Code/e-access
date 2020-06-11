package com.procurement.access.infrastructure.dto.converter

import com.procurement.access.application.model.data.RequestsForEvPanelsResult
import com.procurement.access.infrastructure.dto.cn.RequestsForEvPanelsResponse
import com.procurement.access.infrastructure.dto.cn.criteria.Requirement

fun RequestsForEvPanelsResult.convert(): RequestsForEvPanelsResponse {
    return RequestsForEvPanelsResponse(
        criteria = criteria
            .let { criteria ->
                RequestsForEvPanelsResponse.Criteria(
                    id = criteria.id,
                    title = criteria.title,
                    description = criteria.description,
                    source = criteria.source,
                    relatesTo = criteria.relatesTo,
                    requirementGroups = criteria.requirementGroups
                        .map { requirementGroup ->
                            RequestsForEvPanelsResponse.Criteria.RequirementGroup(
                                id = requirementGroup.id,
                                requirements = requirementGroup.requirements
                                    .map { requirement ->
                                        Requirement(
                                            id = requirement.id,
                                            title = requirement.title,
                                            dataType = requirement.dataType,
                                            value = requirement.value,
                                            period = requirement.period,
                                            description = requirement.description
                                        )
                                    }
                            )
                        }
                )
            }
    )
}
