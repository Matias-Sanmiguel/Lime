package com.uade.lime.property.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "properties")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", length = 30)
    private PropertyType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", length = 30)
    private OperationType operation;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(length = 3)
    private String currency;

    @Column(length = 200)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String province;

    private Integer bedrooms;

    private Integer bathrooms;

    @Column(precision = 10, scale = 2)
    private BigDecimal coveredArea;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalArea;

    @Column(name = "owner_id")
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PropertyStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant deletedAt;

    public static Property draft(String title, String description, PropertyType type,
            OperationType operation, BigDecimal price, String currency, String address, String city,
            String province, Integer bedrooms, Integer bathrooms, BigDecimal coveredArea,
            BigDecimal totalArea, Long ownerID, Instant now) {
        Property property = new Property();
        property.title = title;
        property.description = description;
        property.type = type;
        property.operation = operation;
        property.price = price;
        property.currency = currency;
        property.address = address;
        property.city = city;
        property.province = province;
        property.bedrooms = bedrooms;
        property.bathrooms = bathrooms;
        property.coveredArea = coveredArea;
        property.totalArea = totalArea;
        property.ownerId = ownerID;
        property.status = PropertyStatus.DRAFT;
        property.createdAt = now;
        property.updatedAt = now;
        return property;
    }

    public void delete(Instant now) {
        deletedAt = now;
        updatedAt = now;
    }

    public void publish(Instant now) {
        requireComplete();
        status = PropertyStatus.PUBLISHED;
        updatedAt = now;
    }

    private void requireComplete() { // metodo para verificar que los campos requeridos no sean
                                     // nulos o vacíos antes de publicar la propiedad
        if (isBlank(title) || price == null || isBlank(currency) || isBlank(city) || type == null
                || operation == null) {
            throw new IllegalStateException(
                    "Cannot publish: title, price, currency, city, type and operation are required");
        }
    }

    private static boolean isBlank(String value) { // metodo para verificar si un string es nulo o
                                                   // está vacío
        return value == null || value.isBlank();
    }

    public void pause(Instant now) {
        status = PropertyStatus.PAUSED;
        updatedAt = now;
    }

    public void update(String title, String description, PropertyType type, OperationType operation,
            BigDecimal price, String currency, String address, String city, String province,
            Integer bedrooms, Integer bathrooms, BigDecimal coveredArea, BigDecimal totalArea,
            Instant now) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.operation = operation;
        this.price = price;
        this.currency = currency;
        this.address = address;
        this.city = city;
        this.province = province;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.coveredArea = coveredArea;
        this.totalArea = totalArea;
        this.updatedAt = now;
    }
}
