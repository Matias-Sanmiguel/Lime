package com.uade.lime.property.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.lime.property.dto.PropertyResponse;
import com.uade.lime.property.service.PropertyService;

@RestController
@RequestMapping("/api/v1/me")
public class MeController {

    private final PropertyService service;

    public MeController(PropertyService service) {
        this.service = service;
    }

    @GetMapping("/properties")
    public List<PropertyResponse> listMine(@RequestHeader("X-User-Id") Long ownerId) {
        return service.listMine(ownerId);
    }
}
