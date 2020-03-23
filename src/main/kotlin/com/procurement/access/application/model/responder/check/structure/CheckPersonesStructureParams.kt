package com.procurement.access.application.model.responder.check.structure

import com.procurement.access.domain.fail.error.DataErrors
import com.procurement.access.domain.model.date.tryParse
import com.procurement.access.domain.model.document.DocumentId
import com.procurement.access.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.access.domain.model.enums.BusinessFunctionType
import com.procurement.access.domain.model.enums.LocationOfPersonsType
import com.procurement.access.domain.util.Option
import com.procurement.access.domain.util.Result
import com.procurement.access.domain.util.Result.Companion.failure
import com.procurement.access.lib.toSetBy
import java.time.LocalDateTime

class CheckPersonesStructureParams private constructor(
    val cpid: String,
    val ocid: String,
    val persons: List<Person>,
    val locationOfPersones: LocationOfPersonsType
) {
    companion object {
        fun tryCreate(
            cpid: String,
            ocid: String,
            persons: List<Person>,
            locationOfPersones: String
        ): Result<CheckPersonesStructureParams, DataErrors> {

            val parsedType = locationOfPersones
                .let {
                    val locationOfPersonsType = LocationOfPersonsType.orNull(it)
                        ?: return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "locationOfPersones",
                                expectedValues = allowedLocationOfPersonsTypes,
                                actualValue = it
                            )
                        )

                    if (locationOfPersonsType.key !in allowedLocationOfPersonsTypes)
                        return failure(
                            DataErrors.Validation.UnknownValue(
                                name = "locationOfPersones",
                                expectedValues = allowedLocationOfPersonsTypes,
                                actualValue = it
                            )
                        )
                    else
                        locationOfPersonsType

                }

            return Result.success(
                CheckPersonesStructureParams(
                    cpid = cpid,
                    ocid = ocid,
                    persons = persons,
                    locationOfPersones = parsedType
                )
            )
        }
    }

    class Person private constructor(
        val title: String,
        val name: String,
        val identifier: Identifier,
        val businessFunctions: List<BusinessFunction>
    ) {

        companion object {
            fun tryCreate(
                title: String,
                name: String,
                identifier: Identifier,
                businessFunctions: List<BusinessFunction>
            ): Result<Person, DataErrors> {

                return Result.success(
                    Person(
                        title = title,
                        name = name,
                        identifier = identifier,
                        businessFunctions = businessFunctions
                    )
                )
            }
        }

        class Identifier private constructor(
            val scheme: String,
            val id: String,
            val uri: String?
        ) {

            companion object {
                fun tryCreate(
                    scheme: String,
                    id: String,
                    uri: String?
                ): Result<Identifier, DataErrors> {

                    return Result.success(
                        Identifier(
                            scheme = scheme,
                            id = id,
                            uri = uri
                        )
                    )
                }
            }
        }

        class BusinessFunction private constructor(
            val id: String,
            val type: BusinessFunctionType,
            val jobTitle: String,
            val period: Period,
            val documents: List<Document>
        ) {

            companion object {
                fun tryCreate(
                    id: String,
                    type: String,
                    jobTitle: String,
                    period: Period,
                    documents: Option<List<Document>>
                ): Result<BusinessFunction, DataErrors> {

                    val parsedType = type
                        .let {
                            val businessFunctionType = BusinessFunctionType.orNull(it)
                                ?: return failure(
                                    DataErrors.Validation.UnknownValue(
                                        name = "businessFunction.type",
                                        expectedValues = allowedBusinessFunctionTypes,
                                        actualValue = it
                                    )
                                )

                            if (businessFunctionType.key !in allowedBusinessFunctionTypes)
                                return failure(
                                    DataErrors.Validation.UnknownValue(
                                        name = "businessFunction.type",
                                        expectedValues = allowedBusinessFunctionTypes,
                                        actualValue = it
                                    )
                                )
                            else
                                businessFunctionType
                        }

                    return Result.success(
                        BusinessFunction(
                            id = id,
                            type = parsedType,
                            jobTitle = jobTitle,
                            period = period,
                            documents = documents.get
                        )
                    )
                }
            }

            class Period private constructor(
                val startDate: LocalDateTime
            ) {

                companion object {
                    fun tryCreate(
                        startDate: String
                    ): Result<Period, DataErrors> {

                        val startDateParsed = startDate.tryParse()
                            .doOnError { expectedFormat ->
                                return failure(
                                    DataErrors.Validation.DataFormatMismatch(
                                        name = "startDate",
                                        actualValue = startDate,
                                        expectedFormat = expectedFormat
                                    )
                                )
                            }
                            .get

                        return Result.success(
                            Period(
                                startDate = startDateParsed
                            )
                        )
                    }
                }
            }

            class Document private constructor(
                val id: DocumentId,
                val documentType: BusinessFunctionDocumentType,
                val title: String,
                val description: String?
            ) {

                companion object {
                    fun tryCreate(
                        id: String,
                        documentType: String,
                        title: String,
                        description: String?
                    ): Result<Document, DataErrors> {

                        val createdDocumentType = documentType
                            .let {
                                val businessFunctionDocumentType = BusinessFunctionDocumentType.orNull(it)
                                    ?: return failure(
                                        DataErrors.Validation.UnknownValue(
                                            name = "documentType",
                                            expectedValues = allowedBusinessFunctionDocumentTypes,
                                            actualValue = it
                                        )
                                    )
                                if (businessFunctionDocumentType.key !in allowedBusinessFunctionDocumentTypes)
                                    return failure(
                                        DataErrors.Validation.UnknownValue(
                                            name = "documentType",
                                            expectedValues = allowedBusinessFunctionDocumentTypes,
                                            actualValue = it
                                        )
                                    )
                                else
                                    businessFunctionDocumentType
                            }

                        return Result.success(
                            Document(
                                id = id,
                                documentType = createdDocumentType,
                                title = title,
                                description = description
                            )
                        )
                    }
                }
            }
        }
    }
}

val allowedLocationOfPersonsTypes = LocationOfPersonsType.values().filter {
    when (it) {
        LocationOfPersonsType.REQUIREMENT_RESPONSE -> true
    }
}.toSetBy { it.key }

val allowedBusinessFunctionTypes = BusinessFunctionType.values().filter {
    when (it) {
        BusinessFunctionType.CHAIRMAN,
        BusinessFunctionType.PROCURMENT_OFFICER,
        BusinessFunctionType.CONTACT_POINT,
        BusinessFunctionType.TECHNICAL_EVALUATOR,
        BusinessFunctionType.TECHNICAL_OPENER,
        BusinessFunctionType.PRICE_OPENER,
        BusinessFunctionType.PRICE_EVALUATOR -> true
        BusinessFunctionType.AUTHORITY       -> false
    }
}.toSetBy { it.key }

val allowedBusinessFunctionDocumentTypes = BusinessFunctionDocumentType.values().filter {
    when (it) {
        BusinessFunctionDocumentType.REGULATORY_DOCUMENT -> true
    }
}.toSetBy { it.key }
