package com.uade.lime.property.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.uade.lime.auth.model.User;
import com.uade.lime.auth.repository.UserRepository;
import com.uade.lime.auth.security.UserPrincipal;
import com.uade.lime.property.dto.CreateImageRequest;
import com.uade.lime.property.dto.CreateInquiryRequest;
import com.uade.lime.property.dto.CreatePropertyRequest;
import com.uade.lime.property.dto.ImageResponse;
import com.uade.lime.property.dto.InquiryResponse;
import com.uade.lime.property.dto.OwnerResponse;
import com.uade.lime.property.dto.PageResponse;
import com.uade.lime.property.dto.PropertyResponse;
import com.uade.lime.property.dto.UpdatePropertyRequest;
import com.uade.lime.property.model.Inquiry;
import com.uade.lime.property.model.OperationType;
import com.uade.lime.property.model.Property;
import com.uade.lime.property.model.PropertyImage;
import com.uade.lime.property.model.PropertyStatus;
import com.uade.lime.property.model.PropertyType;
import com.uade.lime.property.repository.InquiryRepository;
import com.uade.lime.property.repository.PropertyImageRepository;
import com.uade.lime.property.repository.PropertyRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class PropertyService {

    private final PropertyRepository repository;
    private final PropertyImageRepository imageRepository;
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    public PropertyService(
            PropertyRepository repository,
            PropertyImageRepository imageRepository,
            InquiryRepository inquiryRepository,
            UserRepository userRepository) {
        this.repository = repository;
        this.imageRepository = imageRepository;
        this.inquiryRepository = inquiryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ImageResponse addImage(Long propertyId, CreateImageRequest request) {
        Property property = findActive(propertyId);
        PropertyImage image = PropertyImage.of(property, request.url(), Instant.now());
        return ImageResponse.from(imageRepository.save(image));
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

        Page<Property> propertyPage = repository.findAll(
                filters,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        Map<Long, OwnerResponse> ownersById = userRepository
                .findAllById(propertyPage.getContent().stream().map(Property::getOwnerId).distinct().toList())
                .stream()
                .collect(Collectors.toMap(User::getId, OwnerResponse::from));
        Page<PropertyResponse> result = propertyPage
                .map(property -> PropertyResponse.from(property, ownersById.get(property.getOwnerId())));
        return PageResponse.from(result);
    }

    @Transactional
    public PropertyResponse create(CreatePropertyRequest request, UserPrincipal owner) {
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
                owner.id(),
                now);
        return PropertyResponse.from(repository.save(property), ownerResponseOf(owner));
    }

    @Transactional(readOnly = true)
    public PropertyResponse get(Long id) {
        Property property = findActive(id);
        OwnerResponse owner = userRepository.findById(property.getOwnerId())
                .map(OwnerResponse::from)
                .orElse(null);
        return PropertyResponse.from(property, owner);
    }

    @Transactional(readOnly = true)
    public PageResponse<PropertyResponse> listMine(
            UserPrincipal user,
            int page,
            int size,
            String city,
            PropertyType type,
            OperationType operation,
            PropertyStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String province,
            Integer minBedrooms,
            Integer minBathrooms) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPrice cannot be greater than maxPrice");
        }

        Specification<Property> filters = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.isNull(root.get("deletedAt")));
            predicates.add(builder.equal(root.get("ownerId"), user.id()));
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
            if (province != null && !province.isBlank()) {
                predicates.add(builder.equal(builder.lower(root.get("province")), province.trim().toLowerCase()));
            }
            if (minBedrooms != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("bedrooms"), minBedrooms));
            }
            if (minBathrooms != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("bathrooms"), minBathrooms));
            }
            return builder.and(predicates.toArray(Predicate[]::new));
        };

        Page<Property> propertyPage = repository.findAll(
                filters,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        OwnerResponse owner = ownerResponseOf(user);
        Page<PropertyResponse> result = propertyPage.map(property -> PropertyResponse.from(property, owner));
        return PageResponse.from(result);
    }

    @Transactional
    public PropertyResponse update(Long id, UpdatePropertyRequest request, UserPrincipal user) {
        Property property = findActive(id);
        requireOwner(property, user.id());
        if (!hasUpdates(request)) {
            return PropertyResponse.from(property, ownerResponseOf(user));
        }

        property.update(
                request.title() != null ? request.title() : property.getTitle(),
                request.description() != null ? request.description() : property.getDescription(),
                request.type() != null ? request.type() : property.getType(),
                request.operation() != null ? request.operation() : property.getOperation(),
                request.price() != null ? request.price() : property.getPrice(),
                request.currency() != null ? normalizeCurrency(request.currency()) : property.getCurrency(),
                request.address() != null ? request.address() : property.getAddress(),
                request.city() != null ? request.city() : property.getCity(),
                request.province() != null ? request.province() : property.getProvince(),
                request.bedrooms() != null ? request.bedrooms() : property.getBedrooms(),
                request.bathrooms() != null ? request.bathrooms() : property.getBathrooms(),
                request.coveredArea() != null ? request.coveredArea() : property.getCoveredArea(),
                request.totalArea() != null ? request.totalArea() : property.getTotalArea(),
                Instant.now());
        return PropertyResponse.from(property, ownerResponseOf(user));
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Property property = findActive(id);
        requireOwner(property, userId);
        property.delete(Instant.now());
    }

    @Transactional(readOnly = true)
    public List<InquiryResponse> listMyInquiries(Long ownerId) {
        return inquiryRepository.findByPropertyOwnerId(ownerId).stream()
                .map(InquiryResponse::from)
                .toList();
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

    @Transactional
    public PropertyResponse publish(Long id, UserPrincipal user) {
        Property property = findActive(id);
        requireOwner(property, user.id());
        PropertyStatus current = property.getStatus();
        if (current != PropertyStatus.DRAFT && current != PropertyStatus.PAUSED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot publish a property in status " + current);
        }
        property.publish(Instant.now());
        return PropertyResponse.from(property, ownerResponseOf(user));
    }

    @Transactional
    public PropertyResponse pause(Long id, UserPrincipal user) {
        Property property = findActive(id);
        requireOwner(property, user.id());
        if (property.getStatus() != PropertyStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot pause a property in status " + property.getStatus());
        }
        property.pause(Instant.now());
        return PropertyResponse.from(property, ownerResponseOf(user));
    }

    private void requireOwner(Property property, Long userId) {
        if (!property.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this property");
        }
    }

    private OwnerResponse ownerResponseOf(UserPrincipal user) {
        return new OwnerResponse(user.id(), user.name(), user.role(), user.agencyName());
    }

    private boolean hasUpdates(UpdatePropertyRequest request) {
        return request.title() != null
                || request.description() != null
                || request.type() != null
                || request.operation() != null
                || request.price() != null
                || request.currency() != null
                || request.address() != null
                || request.city() != null
                || request.province() != null
                || request.bedrooms() != null
                || request.bathrooms() != null
                || request.coveredArea() != null
                || request.totalArea() != null;
    }
}
