package com.example.kotlin.fakes

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class TestClock(private var baseClock: Clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())) : Clock() {
    override fun getZone(): ZoneId = baseClock.zone

    override fun withZone(zone: ZoneId): Clock = baseClock.withZone(zone)

    override fun instant(): Instant = Instant.now(baseClock)

    fun tickBy(duration: Duration) {
        baseClock = offset(baseClock, duration)
    }
}