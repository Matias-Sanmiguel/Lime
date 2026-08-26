package com.uade.lime.property.controller;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.lime.property.dto.CreateImageRequest;
import com.uade.lime.property.dto.CreatePropertyRequest;
import com.uade.lime.property.dto.ImageResponse;
import com.uade.lime.property.dto.PageResponse;
import com.uade.lime.property.dto.PropertyResponse;
import com.uade.lime.property.model.OperationType;
import com.uade.lime.property.model.PropertyStatus;
import com.uade.lime.property.model.PropertyType;
import com.uade.lime.property.service.PropertyService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/api/v1/properties")
@Validated
public class PropertyController {

    private final PropertyService service;

    public PropertyController(PropertyService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<PropertyResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) OperationType operation,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(required = false) @PositiveOrZero BigDecimal minPrice,
            @RequestParam(required = false) @PositiveOrZero BigDecimal maxPrice) {
        return service.list(page, size, city, type, operation, status, minPrice, maxPrice);
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> create(@Valid @RequestBody CreatePropertyRequest request) {
        PropertyResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/properties/" + created.id())).body(created);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ImageResponse> addImage(@PathVariable Long id, @Valid @RequestBody CreateImageRequest request) {
        ImageResponse created = service.addImage(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public PropertyResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
