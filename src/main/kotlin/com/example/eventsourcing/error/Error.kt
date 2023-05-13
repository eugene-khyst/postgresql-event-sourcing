package com.example.eventsourcing.error;

public class Error extends RuntimeException {

    public Error(String message, Object... args) {
        super(message.formatted(args));
    }
}
