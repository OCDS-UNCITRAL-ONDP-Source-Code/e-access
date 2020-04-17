package com.procurement.access.infrastructure.dto.converter.get.organization

import com.procurement.access.application.model.organization.GetOrganization
import com.procurement.access.domain.fail.error.DataErrors
import com.procurement.access.domain.util.Result
import com.procurement.access.infrastructure.handler.get.organization.GetOrganizationRequest

fun GetOrganizationRequest.convert(): Result<GetOrganization.Params, DataErrors> =
    GetOrganization.Params.tryCreate(cpid = this.cpid, ocid = this.ocid, role = this.role)

