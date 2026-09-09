package com.uade.lime.property.controller;

import com.uade.lime.common.ResourceNotFoundException;
import com.uade.lime.property.model.Inquiry;
import com.uade.lime.property.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/me/inquiries")
public class InquiryController {

    @Autowired
    private InquiryRepository inquiryRepository;

    @GetMapping
    public ResponseEntity<InquiryInboxResponse> getMyInquiries(
            @RequestParam(required = false, defaultValue = "false") boolean unreadOnly,
            Pageable pageable,
            Authentication authentication) {

        Long ownerId = getUserIdFromAuth(authentication);

        Page<Inquiry> inquiries;
        if (unreadOnly) {
            inquiries = inquiryRepository.findByOwnerIdAndReadAtIsNull(ownerId, pageable);
        } else {
            inquiries = inquiryRepository.findByOwnerId(ownerId, pageable);
        }

        long unreadCount = inquiryRepository.countByOwnerIdAndReadAtIsNull(ownerId);

        return ResponseEntity.ok(new InquiryInboxResponse(inquiries, unreadCount));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inquiry> getInquiryById(
            @PathVariable Long id,
            Authentication authentication) {

        Long ownerId = getUserIdFromAuth(authentication);

        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con ID: " + id));

        if (!Objects.equals(inquiry.getOwnerId(), ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(inquiry);
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Inquiry> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        Long ownerId = getUserIdFromAuth(authentication);

        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta no encontrada con ID: " + id));

        if (!Objects.equals(inquiry.getOwnerId(), ownerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        inquiry.setReadAt(LocalDateTime.now());
        Inquiry updatedInquiry = inquiryRepository.save(inquiry);

        return ResponseEntity.ok(updatedInquiry);
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        // Reemplazá este casteo por la forma exacta en que obtienen el usuario en tu proyecto (ej: CustomUserDetails)
        return 1L; 
    }
}

record InquiryInboxResponse(Page<Inquiry> inquiries, long unreadCount) {}