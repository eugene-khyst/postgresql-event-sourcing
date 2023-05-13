package com.example.eventsourcing.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class WaypointDto(
    val address: String,
    @JsonProperty("lat") val latitude: Double,
    @JsonProperty("lon") val longitude: Double
)
