package com.procurement.access.infrastructure.dto.ap.create

import com.procurement.access.infrastructure.AbstractDTOTestBase
import com.procurement.access.infrastructure.handler.v1.model.request.ApCreateRequest
import org.junit.jupiter.api.Test

class CreateApRequestTest : AbstractDTOTestBase<ApCreateRequest>(
    ApCreateRequest::class.java) {
    @Test
    fun fully() {
        testBindingAndMapping("json/dto/ap/create/request/request_create_ap_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/dto/ap/create/request/request_create_ap_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/dto/ap/create/request/request_create_ap_required_2.json")
    }
}
