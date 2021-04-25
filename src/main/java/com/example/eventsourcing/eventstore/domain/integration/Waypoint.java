package com.example.eventsourcing.eventstore.domain.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class Waypoint {

  @JsonProperty("ADDRESS")
  String address;

  @JsonProperty("LAT")
  double latitude;

  @JsonProperty("LON")
  double longitude;
}
