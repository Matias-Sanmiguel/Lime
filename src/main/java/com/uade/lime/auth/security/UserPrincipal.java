package com.uade.lime.auth.security;

import java.time.Instant;

import com.uade.lime.auth.model.UserRole;

public record UserPrincipal(
        Long id,
        String email,
        String name,
        UserRole role,
        String agencyName,
        String jti,
        Instant expiresAt) {
}
