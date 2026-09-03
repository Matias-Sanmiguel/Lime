package com.uade.lime.property.repository;

import com.uade.lime.property.model.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    Page<Inquiry> findByPropertyOwnerIdOrderByCreatedAtDesc(Long ownerId, Pageable pageable);
}
