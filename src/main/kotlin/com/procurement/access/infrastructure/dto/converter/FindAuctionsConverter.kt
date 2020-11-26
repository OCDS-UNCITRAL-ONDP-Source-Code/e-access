package com.procurement.access.infrastructure.dto.converter

import com.procurement.access.application.model.params.FindAuctionsParams
import com.procurement.access.domain.fail.error.DataErrors
import com.procurement.access.infrastructure.handler.find.auction.FindAuctionsRequest
import com.procurement.access.lib.functional.Result

fun FindAuctionsRequest.convert(): Result<FindAuctionsParams, DataErrors> =
    FindAuctionsParams.tryCreate(cpid, ocid)

