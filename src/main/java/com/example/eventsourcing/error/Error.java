package com.example.eventsourcing.error;

import lombok.NonNull;

public class Error extends RuntimeException {

    public Error(@NonNull String message, Object... args) {
        super(message.formatted(args));
    }
}
