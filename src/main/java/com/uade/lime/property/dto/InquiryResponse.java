package com.uade.lime.property.dto;

import java.time.Instant;

import com.uade.lime.property.model.Inquiry;

public record InquiryResponse(
        Long id,
        Long propertyId,
        String name,
        String email,
        String phone,
        String message,
        Instant createdAt) {

    public static InquiryResponse from(Inquiry inquiry) {
        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getProperty().getId(),
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getPhone(),
                inquiry.getMessage(),
                inquiry.getCreatedAt());
    }
}