package com.example.eventsourcing.domain

import java.util.*

enum class AggregateType(val aggregateClass: Class<out Aggregate>) {
    ORDER(OrderAggregate::class.java);

    fun <T : Aggregate> newInstance(aggregateId: UUID): T {
        return try {
            val constructor = aggregateClass.getDeclaredConstructor(
                UUID::class.java, Integer.TYPE
            )
            constructor.newInstance(aggregateId, 0) as T
        } catch (e: ReflectiveOperationException) {
            throw RuntimeException(e)
        }
    }
}
