package com.procurement.access.infrastructure.handler.v1.converter

import com.procurement.access.application.model.responder.processing.ResponderProcessing
import com.procurement.access.domain.fail.error.DataErrors
import com.procurement.access.infrastructure.handler.v2.model.request.ResponderProcessingRequest
import com.procurement.access.lib.extension.mapOptionalResult
import com.procurement.access.lib.extension.mapResult
import com.procurement.access.lib.functional.Result

fun ResponderProcessingRequest.Params.convert(): Result<ResponderProcessing.Params, DataErrors> {

    val responder = this.responder
        .convert()
        .onFailure { return it }

    return ResponderProcessing.Params.tryCreate(
        cpid = this.cpid,
        ocid = this.ocid,
        date = this.date,
        responder = responder
    )
}

private fun ResponderProcessingRequest.Params.Responder.convert(): Result<ResponderProcessing.Params.Responder, DataErrors> {
    val identifier = this.identifier
        .convert()
        .onFailure { return it }

    val businessFunctions = this.businessFunctions
        .mapResult { it.convert() }
        .onFailure { return it }

    return ResponderProcessing.Params.Responder.tryCreate(
        id = this.id,
        title = this.title,
        name = this.name,
        identifier = identifier,
        businessFunctions = businessFunctions
    )
}

private fun ResponderProcessingRequest.Params.Responder.BusinessFunction.convert(): Result<ResponderProcessing.Params.Responder.BusinessFunction, DataErrors> {
    val period = this.period
        .convert()
        .onFailure { return it }

    val documents = this.documents
        .mapOptionalResult { it.convert() }
        .onFailure { return it }

    return ResponderProcessing.Params.Responder.BusinessFunction.tryCreate(
        id = this.id,
        jobTitle = this.jobTitle,
        type = this.type,
        period = period,
        documents = documents
    )
}

private fun ResponderProcessingRequest.Params.Responder.BusinessFunction.Document.convert(): Result<ResponderProcessing.Params.Responder.BusinessFunction.Document, DataErrors> =
    ResponderProcessing.Params.Responder.BusinessFunction.Document.tryCreate(
        id = this.id,
        title = this.title,
        description = this.description,
        documentType = this.documentType
    )

private fun ResponderProcessingRequest.Params.Responder.BusinessFunction.Period.convert(): Result<ResponderProcessing.Params.Responder.BusinessFunction.Period, DataErrors> =
    ResponderProcessing.Params.Responder.BusinessFunction.Period.tryCreate(
        startDate = this.startDate
    )

private fun ResponderProcessingRequest.Params.Responder.Identifier.convert(): Result<ResponderProcessing.Params.Responder.Identifier, DataErrors> =
    ResponderProcessing.Params.Responder.Identifier.tryCreate(
        id = this.id,
        scheme = this.scheme,
        uri = this.uri
    )
