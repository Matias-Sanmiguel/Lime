package com.uade.lime.property.dto;

import java.math.BigDecimal;

import com.uade.lime.property.model.OperationType;
import com.uade.lime.property.model.PropertyType;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record UpdatePropertyRequest(
        @Size(max = 120) String title,
        @Size(max = 2000) String description,
        PropertyType type,
        OperationType operation,
        @PositiveOrZero BigDecimal price,
        @Size(max = 3) String currency,
        @Size(max = 200) String address,
        @Size(max = 100) String city,
        @Size(max = 100) String province,
        @PositiveOrZero Integer bedrooms,
        @PositiveOrZero Integer bathrooms,
        @PositiveOrZero BigDecimal coveredArea,
        @PositiveOrZero BigDecimal totalArea) {
}
