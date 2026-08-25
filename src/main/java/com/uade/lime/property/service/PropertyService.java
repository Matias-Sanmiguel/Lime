package com.uade.lime.property.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.uade.lime.property.dto.CreateInquiryRequest;
import com.uade.lime.property.dto.CreatePropertyRequest;
import com.uade.lime.property.dto.InquiryResponse;
import com.uade.lime.property.dto.PageResponse;
import com.uade.lime.property.dto.PropertyResponse;
import com.uade.lime.property.model.Inquiry;
import com.uade.lime.property.model.OperationType;
import com.uade.lime.property.model.Property;
import com.uade.lime.property.model.PropertyStatus;
import com.uade.lime.property.model.PropertyType;
import com.uade.lime.property.repository.InquiryRepository;
import com.uade.lime.property.repository.PropertyRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class PropertyService {

    private final PropertyRepository repository;
    private final InquiryRepository inquiryRepository;

    public PropertyService(PropertyRepository repository, InquiryRepository inquiryRepository) {
        this.repository = repository;
        this.inquiryRepository = inquiryRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<PropertyResponse> list(
            int page,
            int size,
            String city,
            PropertyType type,
            OperationType operation,
            PropertyStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPrice cannot be greater than maxPrice");
        }

        Specification<Property> filters = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.isNull(root.get("deletedAt")));
            if (city != null && !city.isBlank()) {
                predicates.add(builder.equal(builder.lower(root.get("city")), city.trim().toLowerCase()));
            }
            if (type != null) {
                predicates.add(builder.equal(root.get("type"), type));
            }
            if (operation != null) {
                predicates.add(builder.equal(root.get("operation"), operation));
            }
            if (status != null) {
                predicates.add(builder.equal(root.get("status"), status));
            }
            if (minPrice != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };

        Page<PropertyResponse> result = repository.findAll(
                filters,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(PropertyResponse::from);
        return PageResponse.from(result);
    }

    @Transactional
    public PropertyResponse create(CreatePropertyRequest request) {
        Instant now = Instant.now();
        Property property = Property.draft(
                request.title(),
                request.description(),
                request.type(),
                request.operation(),
                request.price(),
                normalizeCurrency(request.currency()),
                request.address(),
                request.city(),
                request.province(),
                request.bedrooms(),
                request.bathrooms(),
                request.coveredArea(),
                request.totalArea(),
                now);
        return PropertyResponse.from(repository.save(property));
    }

    @Transactional(readOnly = true)
    public PropertyResponse get(Long id) {
        return PropertyResponse.from(findActive(id));
    }

    @Transactional
    public void delete(Long id) {
        findActive(id).delete(Instant.now());
    }

    @Transactional
    public InquiryResponse createInquiry(Long propertyId, CreateInquiryRequest request) {
        Property property = findActive(propertyId);
        Inquiry inquiry = Inquiry.create(
                property,
                request.name(),
                request.email(),
                request.phone(),
                request.message(),
                Instant.now());
        return InquiryResponse.from(inquiryRepository.save(inquiry));
    }

    private Property findActive(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    }

    private String normalizeCurrency(String currency) {
        return currency == null ? null : currency.trim().toUpperCase();
    }
}