package com.uade.lime.property.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.uade.lime.property.model.OperationType;
import com.uade.lime.property.model.Property;
import com.uade.lime.property.model.PropertyStatus;
import com.uade.lime.property.model.PropertyType;

public record PropertyResponse(
        Long id,
        String title,
        String description,
        PropertyType type,
        OperationType operation,
        BigDecimal price,
        String currency,
        String address,
        String city,
        String province,
        Integer bedrooms,
        Integer bathrooms,
        BigDecimal coveredArea,
        BigDecimal totalArea,
        PropertyStatus status,
        Instant createdAt,
        Instant updatedAt) {

    public static PropertyResponse from(Property property) {
        return new PropertyResponse(
                property.getId(),
                property.getTitle(),
                property.getDescription(),
                property.getType(),
                property.getOperation(),
                property.getPrice(),
                property.getCurrency(),
                property.getAddress(),
                property.getCity(),
                property.getProvince(),
                property.getBedrooms(),
                property.getBathrooms(),
                property.getCoveredArea(),
                property.getTotalArea(),
                property.getStatus(),
                property.getCreatedAt(),
                property.getUpdatedAt());
    }
}
