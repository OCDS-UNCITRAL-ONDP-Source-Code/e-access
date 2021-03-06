package com.procurement.access.infrastructure.dto.lot

import com.procurement.access.infrastructure.AbstractDTOTestBase
import com.procurement.access.infrastructure.handler.v1.model.request.LotsForAuctionRequest
import org.junit.jupiter.api.Test

class LotsForAuctionRequestTest : AbstractDTOTestBase<LotsForAuctionRequest>(LotsForAuctionRequest::class.java) {
    @Test
    fun test() {
        testBindingAndMapping("json/dto/lot/request/request_lots_for_auction_full.json")
    }
}
