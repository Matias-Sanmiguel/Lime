package com.uade.lime.property.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.lime.property.dto.InquiryResponse;
import com.uade.lime.property.service.PropertyService;

@RestController
@RequestMapping("/api/v1/me/inquiries")
public class InquiryController {

    private final PropertyService service;

    public InquiryController(PropertyService service) {
        this.service = service;
    }

    @GetMapping
    public List<InquiryResponse> listMine(@RequestHeader("X-User-Id") Long ownerId) {
        return service.listMyInquiries(ownerId);
    }
}
