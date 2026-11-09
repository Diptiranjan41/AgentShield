package com.agentshield.identity;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Task AS-001: Agent registration.
 * Written BEFORE the implementation, so it must fail (red) first.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@WithMockUser(roles = "ADMIN")
class AgentRegistrationIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    MockMvc mvc;

    private static String body(String name, String owner) {
        return """
            {"name":"%s","ownerUserId":"%s","role":"FINANCE_ASSISTANT"}
            """.formatted(name, owner);
    }

    private static String uniqueName() {
        return "agent_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @Tag("REQ-ID-001")
    void registerReturns201AndAgentId() throws Exception {
        mvc.perform(post("/api/agents").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(uniqueName(), "user_101")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.agentId").isNotEmpty());
    }

    @Test
    @Tag("REQ-ID-002")
    void everyAgentGetsUniqueId() throws Exception {
        MvcResult a = mvc.perform(post("/api/agents").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(uniqueName(), "user_101")))
            .andExpect(status().isCreated()).andReturn();
        MvcResult b = mvc.perform(post("/api/agents").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(uniqueName(), "user_101")))
            .andExpect(status().isCreated()).andReturn();

        String idA = com.jayway.jsonpath.JsonPath.read(a.getResponse().getContentAsString(), "$.agentId");
        String idB = com.jayway.jsonpath.JsonPath.read(b.getResponse().getContentAsString(), "$.agentId");
        assertThat(idA).isNotEqualTo(idB);
    }

    @Test
    @Tag("REQ-ID-003")
    void duplicateNameReturns409() throws Exception {
        String name = uniqueName();
        mvc.perform(post("/api/agents").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(name, "user_101")))
            .andExpect(status().isCreated());

        mvc.perform(post("/api/agents").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(name, "user_101")))
            .andExpect(status().isConflict());
    }

    @Test
    @Tag("REQ-ID-004")
    void newAgentIsActive() throws Exception {
        mvc.perform(post("/api/agents").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body(uniqueName(), "user_101")))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @Tag("REQ-ID-005")
    void missingNameReturns400() throws Exception {
        mvc.perform(post("/api/agents").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ownerUserId\":\"user_101\"}"))
            .andExpect(status().isBadRequest());
    }
}