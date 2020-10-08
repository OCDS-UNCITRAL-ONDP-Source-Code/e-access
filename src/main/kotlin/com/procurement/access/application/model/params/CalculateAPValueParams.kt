package com.procurement.access.application.model.params

import com.procurement.access.application.model.parseCpid
import com.procurement.access.application.model.parseOcid
import com.procurement.access.domain.fail.error.DataErrors
import com.procurement.access.domain.model.Cpid
import com.procurement.access.domain.model.Ocid
import com.procurement.access.domain.util.Result
import com.procurement.access.domain.util.Result.Companion.success

class CalculateAPValueParams(
    val cpid: Cpid,
    val ocid: Ocid
) {

    companion object {
        fun tryCreate(cpid: String, ocid: String): Result<CalculateAPValueParams, DataErrors.Validation.DataMismatchToPattern> {
            val parsedCpid = parseCpid(value = cpid)
                .orForwardFail { error -> return error }

            val parsedOcid = parseOcid(value = ocid)
                .orForwardFail { error -> return error }

            return success(CalculateAPValueParams(cpid = parsedCpid, ocid = parsedOcid))
        }
    }
}