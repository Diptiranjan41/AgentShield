package com.agentshield.identity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, String> {

    boolean existsByName(String name);
}