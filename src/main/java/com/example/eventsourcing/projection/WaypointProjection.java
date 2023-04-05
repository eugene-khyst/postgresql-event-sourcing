package com.example.eventsourcing.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class WaypointProjection implements Serializable {

    private String address;
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("lon")
    private double longitude;
}
