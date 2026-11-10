package com.agentshield.identity;

import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentService {

    private final AgentRepository agents;

    public AgentService(AgentRepository agents) {
        this.agents = agents;
    }

    /**
     * Registers a new agent. The id is generated here on the server (REQ-ID-002),
     * never taken from the caller.
     */
    @Transactional
    public Agent register(String name, String ownerUserId, String role) {
        if (agents.existsByName(name)) {
            throw new DuplicateAgentException(name);
        }
        Agent agent = new Agent("agt_" + UUID.randomUUID(), name, ownerUserId, role);
        try {
            return agents.saveAndFlush(agent);
        } catch (DataIntegrityViolationException e) {
            // two requests registered the same name at the same time; the DB unique constraint caught it
            throw new DuplicateAgentException(name);
        }
    }
}