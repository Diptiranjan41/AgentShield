package com.agentshield.identity;

public record AgentResponse(String agentId, String name, String ownerUserId, String role, AgentStatus status) {

    public static AgentResponse from(Agent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getName(),
                agent.getOwnerUserId(),
                agent.getRole(),
                agent.getStatus());
    }
}