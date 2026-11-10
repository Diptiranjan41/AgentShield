package com.agentshield.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterAgentRequest(
        @NotBlank
        @Pattern(regexp = "^[A-Za-z0-9_-]{3,64}$", message = "must be 3-64 characters: letters, digits, _ or -")
        String name,

        @NotBlank
        @Size(max = 128)
        String ownerUserId,

        @Size(max = 64)
        String role) {
}