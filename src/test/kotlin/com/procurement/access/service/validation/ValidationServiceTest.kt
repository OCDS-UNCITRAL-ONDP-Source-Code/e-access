package com.procurement.access.service.validation

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.access.application.model.params.CheckEqualPNAndAPCurrencyParams
import com.procurement.access.application.model.params.ValidateClassificationParams
import com.procurement.access.application.repository.TenderProcessRepository
import com.procurement.access.dao.TenderProcessDao
import com.procurement.access.domain.model.Cpid
import com.procurement.access.domain.model.Ocid
import com.procurement.access.domain.util.Result.Companion.success
import com.procurement.access.domain.util.ValidationResult
import com.procurement.access.infrastructure.generator.TenderProcessEntityGenerator
import com.procurement.access.json.loadJson
import com.procurement.access.service.RulesService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ValidationServiceTest {

    private lateinit var tenderProcessDao: TenderProcessDao
    private lateinit var tenderProcessRepository: TenderProcessRepository
    private lateinit var rulesService: RulesService
    private lateinit var validationService: ValidationService

    companion object{
        private val CPID = Cpid.tryCreateOrNull("ocds-t1s2t3-MD-1565251033096")!!
        private val OCID = Ocid.tryCreateOrNull("ocds-b3wdp1-MD-1581509539187-EV-1581509653044")!!
        private val CPID_AP = Cpid.tryCreateOrNull("ocds-t1s2t3-OT-1565251033097")!!
        private val OCID_AP = Ocid.tryCreateOrNull("ocds-b3wdp1-MD-1581509539187-FE-1581509653045")!!
        private val VALID_CLASSIFICATION_ID = "7a6cd0c8-006d-474b-8ab3-7a2a6cb9aa22"
        private val STORED_CLASSIFICATION_ID = "7a6b670f-858b-4bed-8f19-dc5886b03ca4"

    }

    @BeforeEach
    fun init() {
        tenderProcessDao = mock()
        tenderProcessRepository = mock()
        rulesService = mock()
        validationService = ValidationService(tenderProcessDao, tenderProcessRepository, rulesService)
    }

    @Nested
    inner class CheckEqualPNAndAPCurrency {

        @Test
        fun currencyMatches_success(){
            val params = getParams()

            val pnJson = loadJson("json/service/check/currency/pn_entity.json")
            val tenderPNProcessEntity = TenderProcessEntityGenerator.generate(data = pnJson)
            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpid, stage = params.ocid.stage))
                .thenReturn(success(tenderPNProcessEntity))

            val apJson = loadJson("json/service/check/currency/ap_entity.json")
            val tenderAPProcessEntity = TenderProcessEntityGenerator.generate(data = apJson)
            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpidAP, stage = params.ocidAP.stage))
                .thenReturn(success(tenderAPProcessEntity))

            val actual =  validationService.checkEqualPNAndAPCurrency(params = getParams())

            assertTrue(actual is ValidationResult.Ok)
        }

        @Test
        fun pnRecordNotFound_fail(){
            val params = getParams()

            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpid, stage = params.ocid.stage))
                .thenReturn(success(null))

            val actual =  validationService.checkEqualPNAndAPCurrency(params).error

            val expectedErrorCode = "VR.COM-1.33.1"
            val expectedErrorMessage = "Tender not found by cpid='${params.cpid}' and ocid='${params.ocid}'."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorMessage, actual.description)
        }

        @Test
        fun apRecordNotFound_fail(){
            val params = getParams()

            val tenderProcessEntity = TenderProcessEntityGenerator.generate(data = "")
            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpid, stage = params.ocid.stage))
                .thenReturn(success(tenderProcessEntity))

            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpidAP, stage = params.ocidAP.stage))
                .thenReturn(success(null))

            val actual =  validationService.checkEqualPNAndAPCurrency(params = getParams()).error

            val expectedErrorCode = "VR.COM-1.33.2"
            val expectedErrorMessage = "Tender not found by cpid='${params.cpidAP}' and ocid='${params.ocidAP}'."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorMessage, actual.description)
        }


        @Test
        fun currencyDoesNotMatch_fail(){
            val params = getParams()

            val pnJson = loadJson("json/service/check/currency/pn_entity.json")
            val tenderPNProcessEntity = TenderProcessEntityGenerator.generate(data = pnJson)
            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpid, stage = params.ocid.stage))
                .thenReturn(success(tenderPNProcessEntity))

            val apJson = loadJson("json/service/check/currency/ap_entity_with_unmatching_currency.json")
            val tenderAPProcessEntity = TenderProcessEntityGenerator.generate(data = apJson)
            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpidAP, stage = params.ocidAP.stage))
                .thenReturn(success(tenderAPProcessEntity))

            val actual =  validationService.checkEqualPNAndAPCurrency(params = getParams()).error

            val expectedErrorCode = "VR.COM-1.33.3"
            val expectedErrorMessage = "PN record currency 'tenderCurrency' does not match AP record currency 'unmatchingTenderCurrency'."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorMessage, actual.description)
        }

        private fun getParams() = CheckEqualPNAndAPCurrencyParams.tryCreate(
            cpid = CPID.toString(),
            ocid = OCID.toString(),
            cpidAP = CPID_AP.toString(),
            ocidAP = OCID_AP.toString()
        ).get
    }

    @Nested
    inner class ValidateClassification{

        @Test
        fun recordNotFound_fail(){
            val params = getParams(UUID.randomUUID().toString())

            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpid, stage = params.ocid.stage))
                .thenReturn(success(null))

            val actual =  validationService.validateClassification(params).error

            val expectedErrorCode = "VR.COM-1.30.1"
            val expectedErrorMessage = "Tender not found by cpid='${params.cpid}' and ocid='${params.ocid}'."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorMessage, actual.description)
        }

        @Test
        fun classificationDoesNotMatch_fail(){
            val params = getParams(UUID.randomUUID().toString())

            val json = loadJson("json/service/validate/classification/tender_classification_entity.json")
            val tenderProcessEntity = TenderProcessEntityGenerator.generate(data = json)

            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpid, stage = params.ocid.stage))
                .thenReturn(success(tenderProcessEntity))

            val actual =  validationService.validateClassification(params).error

            val expectedErrorCode = "VR.COM-1.30.2"
            val expectedErrorMessage = "First three symbols of received classification id '${params.tender.classification.id}' does not match stored one '$STORED_CLASSIFICATION_ID'."

            assertEquals(expectedErrorCode, actual.code)
            assertEquals(expectedErrorMessage, actual.description)
        }

        @Test
        fun classificationsMatch_success(){
            val params = getParams(VALID_CLASSIFICATION_ID)

            val json = loadJson("json/service/validate/classification/tender_classification_entity.json")
            val tenderProcessEntity = TenderProcessEntityGenerator.generate(data = json)

            whenever(tenderProcessRepository.getByCpIdAndStage(cpid = params.cpid, stage = params.ocid.stage))
                .thenReturn(success(tenderProcessEntity))

            val actual =  validationService.validateClassification(params)

            assertTrue(actual is ValidationResult.Ok)
        }

        private fun getParams(classificationId: String) = ValidateClassificationParams.tryCreate(
            cpid = CPID.toString(),
            ocid = OCID.toString(),
            tender = ValidateClassificationParams.Tender(ValidateClassificationParams.Tender.Classification(id = classificationId))
        ).get
    }


}