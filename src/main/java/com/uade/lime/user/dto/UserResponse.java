package com.uade.lime.user.dto;

public record UserResponse(
        Long id,
        String email,
        String name,
        String role,
        String agencyName) {
}
