package com.procurement.access.service.validation

import com.procurement.access.service.validation.strategy.CheckItemsStrategy
import com.procurement.access.dao.TenderProcessDao
import com.procurement.access.exception.ErrorException
import com.procurement.access.exception.ErrorType
import com.procurement.access.model.dto.bpe.CommandMessage
import com.procurement.access.model.dto.bpe.ResponseDto
import com.procurement.access.model.dto.lots.CheckLotStatusRq
import com.procurement.access.model.dto.ocds.LotStatus
import com.procurement.access.model.dto.ocds.LotStatusDetails
import com.procurement.access.model.dto.ocds.TenderProcess
import com.procurement.access.model.dto.validation.CheckBSRq
import com.procurement.access.model.dto.validation.CheckBid
import com.procurement.access.utils.toObject
import org.springframework.stereotype.Service

@Service
class ValidationService(private val tenderProcessDao: TenderProcessDao) {

    private val checkItemsStrategy =
        CheckItemsStrategy(tenderProcessDao)

    fun checkBid(cm: CommandMessage): ResponseDto {
        val checkDto = toObject(CheckBid::class.java, cm.data)
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val entity = tenderProcessDao.getByCpIdAndStage(cpId, stage) ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        val process = toObject(TenderProcess::class.java, entity.jsonData)
        checkDto.bid.value?.let {
            if (it.currency != process.tender.value.currency) throw ErrorException(ErrorType.INVALID_CURRENCY)
        }
        val lotsId = process.tender.lots.asSequence().map { it.id }.toSet()
        if (!lotsId.containsAll(checkDto.bid.relatedLots)) throw ErrorException(ErrorType.LOT_NOT_FOUND)
        for (lot in process.tender.lots) {
            if (checkDto.bid.relatedLots.contains(lot.id)) {
                if (!(lot.status == LotStatus.ACTIVE && lot.statusDetails == LotStatusDetails.EMPTY)) throw ErrorException(ErrorType.INVALID_LOT_STATUS)
            }
        }
        return ResponseDto(data = "ok")
    }

    fun checkItems(cm: CommandMessage): ResponseDto {
        val response = checkItemsStrategy.check(cm)
        return ResponseDto(data = response)
    }

    fun checkToken(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val token = cm.context.token ?: throw ErrorException(ErrorType.CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(ErrorType.CONTEXT)
        val entity = tenderProcessDao.getByCpIdAndStage(cpId, stage) ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        if (entity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
        if (entity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        return ResponseDto(data = "ok")
    }

    fun checkLotStatus(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val token = cm.context.token ?: throw ErrorException(ErrorType.CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(ErrorType.CONTEXT)
        val lotId = cm.context.id ?: throw ErrorException(ErrorType.CONTEXT)

        val entity = tenderProcessDao.getByCpIdAndStage(cpId, stage) ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        if (entity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
        if (entity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        val process = toObject(TenderProcess::class.java, entity.jsonData)
        process.tender.lots.asSequence()
                .firstOrNull { it.id == lotId && it.status == LotStatus.ACTIVE && it.statusDetails == LotStatusDetails.AWARDED }
                ?: throw ErrorException(ErrorType.NO_AWARDED_LOT)
        return ResponseDto(data = "ok")
    }

    fun checkLotsStatus(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(ErrorType.CONTEXT)
        val lotDto = toObject(CheckLotStatusRq::class.java, cm.data)

        val entity = tenderProcessDao.getByCpIdAndStage(cpId, stage) ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        val process = toObject(TenderProcess::class.java, entity.jsonData)

        val lot = process.tender.lots.asSequence().firstOrNull { it.id == lotDto.relatedLot }
        if (lot != null) {
            if (lot.status != LotStatus.ACTIVE || lot.statusDetails !== LotStatusDetails.EMPTY) {
                throw ErrorException(ErrorType.INVALID_LOT_STATUS)
            }
        } else {
            throw ErrorException(ErrorType.LOT_NOT_FOUND)
        }
        return ResponseDto(data = "Lot status valid.")
    }

    fun checkBudgetSources(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(ErrorType.CONTEXT)
        val bsDto = toObject(CheckBSRq::class.java, cm.data)

        val entity = tenderProcessDao.getByCpIdAndStage(cpId, "EV") ?: throw ErrorException(ErrorType.DATA_NOT_FOUND)
        val process = toObject(TenderProcess::class.java, entity.jsonData)
        val bbIds = process.planning.budget.budgetBreakdown.asSequence().map { it.id }.toHashSet()
        val bsIds = bsDto.planning.budget.budgetSource.asSequence().map { it.budgetBreakdownID }.toHashSet()
        if (!bbIds.containsAll(bsIds)) throw ErrorException(ErrorType.INVALID_BS)
        return ResponseDto(data = "Budget sources are valid.")
    }
}