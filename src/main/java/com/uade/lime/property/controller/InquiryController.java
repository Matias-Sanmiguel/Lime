package com.uade.lime.property.controller;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uade.lime.property.dto.InquiryResponse;
import com.uade.lime.property.model.Inquiry;
import com.uade.lime.property.repository.InquiryRepository;

@RestController
@RequestMapping("/api/v1/me/inquiries")
public class InquiryController {

    private final InquiryRepository inquiryRepository;

    public InquiryController(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    @GetMapping
    public ResponseEntity<Page<InquiryResponse>> getMyInquiries(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InquiryResponse> inquiries = inquiryRepository
                .findByPropertyOwnerIdOrderByCreatedAtDesc(userId, pageable)
                .map(InquiryResponse::from);
        return ResponseEntity.ok(inquiries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponse> getInquiryDetail(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta no encontrada"));

        if (!userId.equals(inquiry.getProperty().getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tenés permisos para ver esta consulta");
        }

        return ResponseEntity.ok(InquiryResponse.from(inquiry));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<InquiryResponse> markAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta no encontrada"));

        if (!userId.equals(inquiry.getProperty().getOwnerId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "No tenés permisos para marcar esta consulta como leída");
        }

        inquiry.markRead(Instant.now());
        return ResponseEntity.ok(InquiryResponse.from(inquiryRepository.save(inquiry)));
    }
}
