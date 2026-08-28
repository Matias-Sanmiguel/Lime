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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.uade.lime.property.dto.CreateImageRequest;
import com.uade.lime.property.dto.CreatePropertyRequest;
import com.uade.lime.property.dto.ImageResponse;
import com.uade.lime.property.dto.PageResponse;
import com.uade.lime.property.dto.PropertyResponse;
import com.uade.lime.property.model.OperationType;
import com.uade.lime.property.model.Property;
import com.uade.lime.property.model.PropertyImage;
import com.uade.lime.property.model.PropertyStatus;
import com.uade.lime.property.model.PropertyType;
import com.uade.lime.property.repository.PropertyImageRepository;
import com.uade.lime.property.repository.PropertyRepository;
import com.uade.lime.property.storage.FileStorageService;

import jakarta.persistence.criteria.Predicate;

@Service
public class PropertyService {

    private final PropertyRepository repository;
    private final PropertyImageRepository imageRepository;
    private final FileStorageService fileStorageService; // nuevo

    public PropertyService(PropertyRepository repository, PropertyImageRepository imageRepository,
            FileStorageService fileStorageService) {
        this.repository = repository;
        this.imageRepository = imageRepository;
        this.fileStorageService = fileStorageService;
    }

    public ImageResponse addImage(Long propertyId, MultipartFile file) {
        Property property = repository.findByIdAndDeletedAtIsNull(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));

        String filename = fileStorageService.store(file);
        String url = "/uploads/" + filename;

        PropertyImage image = PropertyImage.of(property, url, Instant.now());
        PropertyImage saved = imageRepository.save(image);

        return ImageResponse.from(saved);
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

    private Property findActive(Long id) {
        return repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
    }

    private String normalizeCurrency(String currency) {
        return currency == null ? null : currency.trim().toUpperCase();
    }

    public void deleteImage(Long propertyId, Long imageId) {
        PropertyImage image = imageRepository.findByIdAndPropertyId(imageId, propertyId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        String filename = image.getUrl().substring(image.getUrl().lastIndexOf('/') + 1);
        fileStorageService.delete(filename);
        imageRepository.delete(image);
    }

    public ImageResponse replaceImage(Long propertyId, Long imageId, MultipartFile file) {
        PropertyImage image = imageRepository.findByIdAndPropertyId(imageId, propertyId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        String oldFilename = image.getUrl().substring(image.getUrl().lastIndexOf('/') + 1);
        String newFilename = fileStorageService.store(file);

        image.replaceUrl("/uploads/" + newFilename, Instant.now());
        PropertyImage saved = imageRepository.save(image);

        fileStorageService.delete(oldFilename);

        return ImageResponse.from(saved);
    }
}
