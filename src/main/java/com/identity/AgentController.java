package com.agentshield.identity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService service;

    public AgentController(AgentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AgentResponse> register(@Valid @RequestBody RegisterAgentRequest request) {
        Agent agent = service.register(request.name(), request.ownerUserId(), request.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(AgentResponse.from(agent));
    }
}