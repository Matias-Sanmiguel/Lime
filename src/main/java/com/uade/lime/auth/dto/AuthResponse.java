package com.uade.lime.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        int expiresIn,
        UserResponse user) {

    public static AuthResponse of(String token, int expiresIn, UserResponse user) {
        return new AuthResponse(token, "Bearer", expiresIn, user);
    }
}
