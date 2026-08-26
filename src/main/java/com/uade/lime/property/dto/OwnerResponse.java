package com.uade.lime.property.dto;

import com.uade.lime.auth.model.User;
import com.uade.lime.auth.model.UserRole;

public record OwnerResponse(
        Long id,
        String name,
        UserRole role,
        String agencyName) {

    public static OwnerResponse from(User user) {
        return new OwnerResponse(
                user.getId(),
                user.getName(),
                user.getRole(),
                user.getAgencyName());
    }
}
