package com.procurement.access.application.model.criteria

import com.procurement.access.application.model.parseCpid
import com.procurement.access.application.model.parseOcid
import com.procurement.access.domain.fail.error.DataErrors
import com.procurement.access.domain.model.Cpid
import com.procurement.access.domain.model.Ocid
import com.procurement.access.domain.util.Result

class GetQualificationCriteriaAndMethod {

    class Params private constructor(val cpid: Cpid, val ocid: Ocid) {
        companion object {
            fun tryCreate(cpid: String, ocid: String): Result<Params, DataErrors> {
                val cpidResult = parseCpid(value = cpid)
                    .orForwardFail { error -> return error }

                val ocidResult = parseOcid(value = ocid)
                    .orForwardFail { error -> return error }

                return Result.success(Params(cpid = cpidResult, ocid = ocidResult))
            }
        }
    }
}