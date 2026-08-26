package com.uade.lime.property.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.lime.property.model.PropertyImage;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
}