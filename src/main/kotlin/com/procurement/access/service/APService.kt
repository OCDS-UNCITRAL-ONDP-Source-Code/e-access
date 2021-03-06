package com.procurement.access.service

import com.procurement.access.application.model.params.CalculateAPValueParams
import com.procurement.access.application.model.parseCpid
import com.procurement.access.application.repository.TenderProcessRepository
import com.procurement.access.application.service.Logger
import com.procurement.access.application.service.ap.get.GetAPTitleAndDescriptionContext
import com.procurement.access.application.service.ap.get.GetAPTitleAndDescriptionResult
import com.procurement.access.domain.fail.Fail
import com.procurement.access.domain.fail.error.ValidationErrors
import com.procurement.access.domain.model.Cpid
import com.procurement.access.domain.model.enums.RelatedProcessType
import com.procurement.access.domain.model.enums.Stage
import com.procurement.access.exception.ErrorException
import com.procurement.access.exception.ErrorType
import com.procurement.access.infrastructure.entity.APEntity
import com.procurement.access.infrastructure.entity.PNEntity
import com.procurement.access.infrastructure.entity.process.RelatedProcess
import com.procurement.access.infrastructure.handler.v2.model.response.CalculateAPValueResult
import com.procurement.access.lib.functional.Result
import com.procurement.access.lib.functional.Result.Companion.failure
import com.procurement.access.lib.functional.Result.Companion.success
import com.procurement.access.lib.functional.flatMap
import com.procurement.access.utils.toObject
import com.procurement.access.utils.trySerialization
import com.procurement.access.utils.tryToObject
import org.springframework.stereotype.Service
import java.math.BigDecimal

interface APService {
    fun calculateAPValue(params: CalculateAPValueParams): Result<CalculateAPValueResult, Fail>
    fun getAPTitleAndDescription(context: GetAPTitleAndDescriptionContext): GetAPTitleAndDescriptionResult
}

@Service
class APServiceImpl(
    private val tenderProcessRepository: TenderProcessRepository,
    private val logger: Logger
) : APService {

    override fun calculateAPValue(params: CalculateAPValueParams): Result<CalculateAPValueResult, Fail> {

        // FR.COM-1.31.1
        val entity = tenderProcessRepository.getByCpIdAndStage(params.cpid, params.ocid.stage)
            .onFailure { fail -> return fail }
            ?: return failure( // VR.COM-1.31.1
                ValidationErrors.TenderNotFoundOnCalculateAPValue(params.cpid, params.ocid)
            )

        val ap = entity.jsonData.tryToObject(APEntity::class.java)
            .onFailure { fail -> return fail }

        // FR.COM-1.31.2
        val relatedPNProcesses = ap.relatedProcesses.orEmpty()
            .filter(::isRelatedToPN)

        // VR.COM-1.31.2
        if (relatedPNProcesses.isEmpty())
            return failure(ValidationErrors.RelationNotFoundOnCalculateAPValue(params.cpid, params.ocid))

        val relatedPns = relatedPNProcesses.map { pnProcess ->
            parseCpid(pnProcess.identifier)
                .flatMap { parsedCpid -> tenderProcessRepository.getByCpIdAndStage(parsedCpid, Stage.PN) }
                .flatMap { pnEntity -> pnEntity!!.jsonData.tryToObject(PNEntity::class.java) }
                .onFailure { fail -> return fail }
        }

        // FR.COM-1.31.3
        // FR.COM-1.31.4
        val relatedPnsValueSum = relatedPns
            .map { it.tender.value.amount }
            .fold(BigDecimal.ZERO, BigDecimal::add)

        val apTenderValue = ap.tender.value.copy(amount = relatedPnsValueSum)

        val updatedAp = ap.copy(
            tender = ap.tender.copy(
                value = apTenderValue
            )
        )

        val updatedJsonData = trySerialization(updatedAp)
            .onFailure { fail -> return fail }

        val updatedEntity = entity.copy(jsonData = updatedJsonData)

        // FR.COM-1.31.6
        tenderProcessRepository.update(entity = updatedEntity)

        val result = CalculateAPValueResult(
            CalculateAPValueResult.Tender(
                value = CalculateAPValueResult.Tender.Value(
                    amount = apTenderValue.amount!!,
                    currency = apTenderValue.currency
                )
            )
        )

        return success(result)
    }

    override fun getAPTitleAndDescription(context: GetAPTitleAndDescriptionContext): GetAPTitleAndDescriptionResult {
        val cpid = Cpid.tryCreate(context.cpid)
            .orThrow { pattern ->
                ErrorException(
                    error = ErrorType.INCORRECT_VALUE_ATTRIBUTE,
                    message = "cpid '${context.cpid}' mismatch to pattern $pattern."
                )
            }

        /**
         * Why stage getting not from context?,
         * Because this command now used in PCR process, so there is no place when we can get appropriate stage
         */
        val stage = Stage.AP

        val entity = tenderProcessRepository.getByCpIdAndStage(cpid = cpid, stage = stage)
            .orThrow { fail -> throw fail.exception }
            ?: throw ErrorException(
                error = ErrorType.ENTITY_NOT_FOUND,
                message = "VR.COM-1.35.1. Cannot found record by cpid = '$cpid' and stage = '$stage' "
            )

        val ap = toObject(APEntity::class.java, entity.jsonData)

        return GetAPTitleAndDescriptionResult(title = ap.tender.title, description = ap.tender.description)
    }

    private fun isRelatedToPN(relatedProcess: RelatedProcess): Boolean =
        relatedProcess.relationship.any { relationship -> relationship == RelatedProcessType.X_SCOPE }
}
