package com.procurement.access.infrastructure.handler.v1.converter

import com.procurement.access.application.model.params.OutsourcingPNParams
import com.procurement.access.domain.fail.error.DataErrors
import com.procurement.access.infrastructure.handler.v2.model.request.OutsourcingPNRequest
import com.procurement.access.lib.functional.Result

fun OutsourcingPNRequest.convert(): Result<OutsourcingPNParams, DataErrors.Validation.DataMismatchToPattern> =
    OutsourcingPNParams.tryCreate(cpid = this.cpid, ocid = this.ocid, cpidFA = this.cpidFA)
