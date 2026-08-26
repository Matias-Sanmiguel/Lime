package com.uade.lime.auth.dto;

import com.uade.lime.auth.model.User;
import com.uade.lime.auth.model.UserRole;

public record UserResponse(
        Long id,
        String email,
        String name,
        UserRole role,
        String agencyName) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getAgencyName());
    }
}
