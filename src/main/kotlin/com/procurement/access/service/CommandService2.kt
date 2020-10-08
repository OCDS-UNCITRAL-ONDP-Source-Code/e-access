package com.procurement.access.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.access.application.service.Logger
import com.procurement.access.infrastructure.handler.calculate.value.CalculateAPValueHandler
import com.procurement.access.infrastructure.handler.check.accesstotender.CheckAccessToTenderHandler
import com.procurement.access.infrastructure.handler.check.fa.CheckExistenceFAHandler
import com.procurement.access.infrastructure.handler.check.persons.CheckPersonsStructureHandler
import com.procurement.access.infrastructure.handler.check.relation.CheckRelationHandler
import com.procurement.access.infrastructure.handler.check.tenderstate.CheckTenderStateHandler
import com.procurement.access.infrastructure.handler.create.CreateCriteriaForProcuringEntityHandler
import com.procurement.access.infrastructure.handler.create.relation.CreateRelationToOtherProcessHandler
import com.procurement.access.infrastructure.handler.find.auction.FindAuctionsHandler
import com.procurement.access.infrastructure.handler.find.criteria.FindCriteriaHandler
import com.procurement.access.infrastructure.handler.get.criteria.GetQualificationCriteriaAndMethodHandler
import com.procurement.access.infrastructure.handler.get.lotStateByIds.GetLotStateByIdsHandler
import com.procurement.access.infrastructure.handler.get.lotids.FindLotIdsHandler
import com.procurement.access.infrastructure.handler.get.organization.GetOrganizationHandler
import com.procurement.access.infrastructure.handler.get.tender.state.GetTenderStateHandler
import com.procurement.access.infrastructure.handler.pn.OutsourcingPNHandler
import com.procurement.access.infrastructure.handler.processing.responder.ResponderProcessingHandler
import com.procurement.access.infrastructure.handler.set.stateforlots.SetStateForLotsHandler
import com.procurement.access.infrastructure.handler.set.statefortender.SetStateForTenderHandler
import com.procurement.access.infrastructure.handler.validate.ValidateRequirementResponsesHandler
import com.procurement.access.infrastructure.handler.verify.VerifyRequirementResponseHandler
import com.procurement.access.infrastructure.web.dto.ApiResponse
import com.procurement.access.model.dto.bpe.Command2Type
import com.procurement.access.model.dto.bpe.errorResponse
import com.procurement.access.model.dto.bpe.getAction
import com.procurement.access.model.dto.bpe.getId
import com.procurement.access.model.dto.bpe.getVersion
import org.springframework.stereotype.Service

@Service
class CommandService2(
    private val calculateAPValueHandler: CalculateAPValueHandler,
    private val checkAccessToTenderHandler: CheckAccessToTenderHandler,
    private val checkExistenceFAHandler: CheckExistenceFAHandler,
    private val checkPersonsStructureHandler: CheckPersonsStructureHandler,
    private val checkRelationHandler: CheckRelationHandler,
    private val checkTenderStateHandler: CheckTenderStateHandler,
    private val createCriteriaForProcuringEntityHandler: CreateCriteriaForProcuringEntityHandler,
    private val createRelationToOtherProcessHandler: CreateRelationToOtherProcessHandler,
    private val findAuctionsHandler: FindAuctionsHandler,
    private val findCriteriaHandler: FindCriteriaHandler,
    private val findLotIdsHandler: FindLotIdsHandler,
    private val getLotStateByIdsHandler: GetLotStateByIdsHandler,
    private val getOrganizationHandler: GetOrganizationHandler,
    private val getQualificationCriteriaAndMethodHandler: GetQualificationCriteriaAndMethodHandler,
    private val getTenderStateHandler: GetTenderStateHandler,
    private val outsourcingPNHandler: OutsourcingPNHandler,
    private val responderProcessingHandler: ResponderProcessingHandler,
    private val setStateForLotsHandler: SetStateForLotsHandler,
    private val setStateForTenderHandler: SetStateForTenderHandler,
    private val validateRequirementResponsesHandler: ValidateRequirementResponsesHandler,
    private val verifyRequirementResponseHandler: VerifyRequirementResponseHandler,
    private val logger: Logger
) {

    fun execute(request: JsonNode): ApiResponse {

        val version = request.getVersion()
            .doOnError { versionError ->
                val id = request.getId()
                    .doOnError { idError -> return errorResponse(fail = versionError) }
                    .get
                return errorResponse(fail = versionError, id = id)
            }
            .get

        val id = request.getId()
            .doOnError { error -> return errorResponse(fail = error, version = version) }
            .get

        val action = request.getAction()
            .doOnError { error -> return errorResponse(id = id, version = version, fail = error) }
            .get

        val response = when (action) {
            Command2Type.CALCULATE_AP_VALUE -> calculateAPValueHandler.handle(node = request)
            Command2Type.CHECK_ACCESS_TO_TENDER -> checkAccessToTenderHandler.handle(node = request)
            Command2Type.CHECK_EXISTENCE_FA -> checkExistenceFAHandler.handle(node = request)
            Command2Type.CHECK_PERSONES_STRUCTURE -> checkPersonsStructureHandler.handle(node = request)
            Command2Type.CHECK_RELATION -> checkRelationHandler.handle(node = request)
            Command2Type.CHECK_TENDER_STATE -> checkTenderStateHandler.handle(node = request)
            Command2Type.CREATE_CRITERIA_FOR_PROCURING_ENTITY -> createCriteriaForProcuringEntityHandler.handle(node = request)
            Command2Type.CREATE_RELATION_TO_OTHER_PROCESS -> createRelationToOtherProcessHandler.handle(node = request)
            Command2Type.FIND_AUCTIONS -> findAuctionsHandler.handle(node = request)
            Command2Type.FIND_CRITERIA -> findCriteriaHandler.handle(node = request)
            Command2Type.FIND_LOT_IDS -> findLotIdsHandler.handle(node = request)
            Command2Type.GET_LOT_STATE_BY_IDS -> getLotStateByIdsHandler.handle(node = request)
            Command2Type.GET_ORGANIZATION -> getOrganizationHandler.handle(node = request)
            Command2Type.GET_QUALIFICATION_CRITERIA_AND_METHOD -> getQualificationCriteriaAndMethodHandler.handle(node = request)
            Command2Type.GET_TENDER_STATE -> getTenderStateHandler.handle(node = request)
            Command2Type.OUTSOURCING_PN -> outsourcingPNHandler.handle(node = request)
            Command2Type.RESPONDER_PROCESSING -> responderProcessingHandler.handle(node = request)
            Command2Type.SET_STATE_FOR_LOTS -> setStateForLotsHandler.handle(node = request)
            Command2Type.SET_STATE_FOR_TENDER -> setStateForTenderHandler.handle(node = request)
            Command2Type.VALIDATE_REQUIREMENT_RESPONSES -> validateRequirementResponsesHandler.handle(node = request)
            Command2Type.VERIFY_REQUIREMENT_RESPONSE -> verifyRequirementResponseHandler.handle(node = request)
        }

        logger.info("DataOfResponse: '$response'.")
        return response
    }
}
