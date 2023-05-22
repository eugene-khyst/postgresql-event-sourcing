package com.example.eventsourcing.config

import kotlin.time.Duration

data class IntegrationEventProperties(val enabled: Boolean, val delay: Duration)
