package com.uade.lime.property.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.lime.auth.security.UserPrincipal;
import com.uade.lime.property.dto.PropertyResponse;
import com.uade.lime.property.service.PropertyService;
import com.uade.lime.user.dto.UpdateMeRequest;
import com.uade.lime.user.dto.UserResponse;
import com.uade.lime.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    private final PropertyService propertyService;
    private final UserService userService;

    public MeController(PropertyService propertyService, UserService userService) {
        this.propertyService = propertyService;
        this.userService = userService;
    }

    @GetMapping("/properties")
    public List<PropertyResponse> listMine(@RequestHeader("X-User-Id") Long ownerId) {
        return propertyService.listMine(ownerId);
    }

    @GetMapping
    public UserResponse me(@AuthenticationPrincipal UserPrincipal user) {
        return userService.getMe(user.id());
    }

    @PatchMapping
    public UserResponse updateMe(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody UpdateMeRequest request) {
        return userService.updateMe(user.id(), request);
    }
}
