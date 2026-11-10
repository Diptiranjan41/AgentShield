package com.agentshield.identity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @Column(name = "id", length = 64, nullable = false, updatable = false)
    private String id;

    @Column(name = "name", length = 64, nullable = false, unique = true)
    private String name;

    @Column(name = "owner_user_id", length = 128, nullable = false)
    private String ownerUserId;

    @Column(name = "role", length = 64)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false)
    private AgentStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Agent() {
        // required by JPA
    }

    public Agent(String id, String name, String ownerUserId, String role) {
        this.id = id;
        this.name = name;
        this.ownerUserId = ownerUserId;
        this.role = role;
        this.status = AgentStatus.ACTIVE; // REQ-ID-004
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getOwnerUserId() { return ownerUserId; }
    public String getRole() { return role; }
    public AgentStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}