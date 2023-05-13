package com.example.kotlin.functional

import com.example.eventsourcing.domain.event.OrderPlacedEvent
import com.example.eventsourcing.objectMapper
import org.junit.jupiter.api.Test

class MiscTests {

    @Test
    fun `can deserialize order`() {
        val order = """
            {"aggregateId":"a234caf0-f394-4a0f-adae-b00310e7553b",
             "version":1,
             "createdDate":"2023-05-13T10:50:23.290511Z",
            "eventType":"ORDER_PLACED",
            "riderId":"63770803-38f4-4594-aec2-4c74918f7165",
            "price":123.45,
            "route":[{"lat":50.51980052414157,"lon":30.467197278948536,"address":"Kyiv, 17A Polyarna Street"},{"lat":50.48509161169076,"lon":30.485170724431292,"address":"Kyiv, 18V Novokostyantynivska Street"}]}
            """.trimIndent()
        val o = objectMapper.readValue(order, OrderPlacedEvent::class.java)
        println(o)
    }
}
