package com.uade.lime.property.controller;

import com.uade.lime.property.model.Inquiry;
import com.uade.lime.property.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/me/inquiries")
public class InquiryController {
    
    @Autowired
    private InquiryRepository inquiryRepository;

    @GetMapping
    public ResponseEntity<Page<Inquiry>> getMyInquiries(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inquiry> inquiries = inquiryRepository.findByPropertyOwnerIdOrderByCreatedAtDesc(userId, pageable);
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inquiry> getInquiryDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta no encontrada"));

        if (!inquiry.getProperty().getOwnerId().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permisos para ver esta consulta");
        }

        return ResponseEntity.ok(inquiry);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Inquiry> markAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id
    ) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta no encontrada"));

        if (!inquiry.getProperty().getOwnerId().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permisos para marcar esta consulta como leída");
        }

        inquiry.setReadAt(LocalDateTime.now());
        Inquiry savedInquiry = inquiryRepository.save(inquiry);

        return ResponseEntity.ok(savedInquiry);
    }
