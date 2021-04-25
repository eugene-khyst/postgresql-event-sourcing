package com.example.eventsourcing.eventstore.eventsourcing;

public class Error extends RuntimeException {

  public Error(String message) {
    super(message);
  }
}
