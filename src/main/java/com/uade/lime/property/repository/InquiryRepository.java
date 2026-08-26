package com.uade.lime.property.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.lime.property.model.Inquiry;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Query("SELECT i FROM Inquiry i JOIN FETCH i.property p WHERE p.ownerId = :ownerId ORDER BY i.createdAt DESC")
    List<Inquiry> findByPropertyOwnerId(@Param("ownerId") Long ownerId);
}