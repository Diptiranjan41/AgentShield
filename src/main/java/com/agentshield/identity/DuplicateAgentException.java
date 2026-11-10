package com.agentshield.identity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Thrown when an agent name is already registered. Maps to HTTP 409 (REQ-ID-003). */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateAgentException extends RuntimeException {

    public DuplicateAgentException(String name) {
        super("Agent already registered: " + name);
    }
}