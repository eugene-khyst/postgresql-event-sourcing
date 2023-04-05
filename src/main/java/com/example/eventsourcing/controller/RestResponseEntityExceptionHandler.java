package com.example.eventsourcing.controller;

import com.example.eventsourcing.error.Error;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(value = {Error.class})
    protected ResponseEntity<JsonNode> handleError(Error error) {
        return ResponseEntity.badRequest()
                .body(objectMapper.createObjectNode().put("error", error.getMessage()));
    }
}
