package com.uade.lime.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.uade.lime.user.dto.UpdateMeRequest;
import com.uade.lime.user.dto.UserResponse;

@Service
public class UserService {
    public UserResponse getMe(Long userId) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    public UserResponse updateMe(Long userId, UpdateMeRequest request) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
}
