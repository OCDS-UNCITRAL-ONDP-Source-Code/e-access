package com.procurement.access.infrastructure.handler.processing.responder

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.access.application.service.Logger
import com.procurement.access.dao.HistoryDao
import com.procurement.access.domain.fail.Fail
import com.procurement.access.domain.util.Result
import com.procurement.access.infrastructure.dto.converter.convert
import com.procurement.access.infrastructure.handler.AbstractHistoricalHandler
import com.procurement.access.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.access.model.dto.bpe.Command2Type
import com.procurement.access.model.dto.bpe.tryGetParams
import com.procurement.access.model.dto.bpe.tryParamsToObject
import com.procurement.access.service.ResponderService
import org.springframework.stereotype.Service

@Service
class ResponderProcessingHandler(
    private val responderService: ResponderService,
    historyDao: HistoryDao,
    logger: Logger
) : AbstractHistoricalHandler<Command2Type, ResponderProcessingResult>(
    historyRepository = historyDao,
    target = ApiSuccessResponse::class.java,
    logger = logger
) {

    override fun execute(node: JsonNode): Result<ResponderProcessingResult, Fail> {
        val params = node.tryGetParams()
            .doOnError { error -> return Result.failure(error) }
            .get
            .tryParamsToObject(ResponderProcessingRequest.Params::class.java)
            .doOnError { error -> return Result.failure(error) }
            .get
            .convert()
            .doOnError { error -> return Result.failure(error) }
            .get

        return responderService.responderProcessing(params = params)
    }

    override val action: Command2Type
        get() = Command2Type.RESPONDER_PROCESSING
}