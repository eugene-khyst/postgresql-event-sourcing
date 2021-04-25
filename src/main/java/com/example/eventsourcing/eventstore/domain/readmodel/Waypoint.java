package com.example.eventsourcing.eventstore.domain.readmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Waypoint implements Serializable {

  private String address;

  @JsonProperty("lat")
  private double latitude;

  @JsonProperty("lon")
  private double longitude;
}
