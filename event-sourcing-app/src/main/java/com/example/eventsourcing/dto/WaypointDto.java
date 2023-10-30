package com.example.eventsourcing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record WaypointDto(
        String address,
        @JsonProperty("lat")
        double latitude,
        @JsonProperty("lon")
        double longitude
) {
}
