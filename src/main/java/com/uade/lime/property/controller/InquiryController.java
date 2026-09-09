package com.uade.lime.property.controller;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uade.lime.auth.security.UserPrincipal;
import com.uade.lime.property.dto.InquiryInboxResponse;
import com.uade.lime.property.dto.InquiryResponse;
import com.uade.lime.property.dto.MarkInquiryReadRequest;
import com.uade.lime.property.model.Inquiry;
import com.uade.lime.property.model.Property;
import com.uade.lime.property.repository.InquiryRepository;
import com.uade.lime.property.repository.PropertyRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/me/inquiries")
@Validated
public class InquiryController {

    private final InquiryRepository inquiryRepository;
    private final PropertyRepository propertyRepository;

    public InquiryController(InquiryRepository inquiryRepository, PropertyRepository propertyRepository) {
        this.inquiryRepository = inquiryRepository;
        this.propertyRepository = propertyRepository;
    }

    @GetMapping
    public InquiryInboxResponse listMine(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) Long propertyId,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        if (propertyId != null) {
            requireOwnedProperty(propertyId, user.id());
        }

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Inquiry> inquiries = findInboxPage(user.id(), propertyId, unreadOnly, pageable);
        long unreadCount = inquiryRepository.countUnreadByPropertyOwnerId(user.id());

        return InquiryInboxResponse.from(inquiries.map(InquiryResponse::from), unreadCount);
    }

    @GetMapping("/{inquiryId}")
    public InquiryResponse getMine(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long inquiryId) {
        return InquiryResponse.from(requireOwnedInquiry(inquiryId, user.id()));
    }

    @PatchMapping("/{inquiryId}")
    public InquiryResponse markRead(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long inquiryId,
            @Valid @RequestBody MarkInquiryReadRequest request) {
        if (!Boolean.TRUE.equals(request.read())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "read: false is not supported");
        }

        Inquiry inquiry = requireOwnedInquiry(inquiryId, user.id());
        inquiry.markRead(Instant.now());
        inquiryRepository.save(inquiry);
        return InquiryResponse.from(inquiry);
    }

    private Page<Inquiry> findInboxPage(Long ownerId, Long propertyId, boolean unreadOnly, PageRequest pageable) {
        if (propertyId != null && unreadOnly) {
            return inquiryRepository.findUnreadByPropertyOwnerIdAndPropertyId(ownerId, propertyId, pageable);
        }
        if (propertyId != null) {
            return inquiryRepository.findByPropertyOwnerIdAndPropertyId(ownerId, propertyId, pageable);
        }
        if (unreadOnly) {
            return inquiryRepository.findUnreadByPropertyOwnerId(ownerId, pageable);
        }
        return inquiryRepository.findByPropertyOwnerId(ownerId, pageable);
    }

    private void requireOwnedProperty(Long propertyId, Long ownerId) {
        Property property = propertyRepository.findByIdAndDeletedAtIsNull(propertyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        if (!ownerId.equals(property.getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found");
        }
    }

    private Inquiry requireOwnedInquiry(Long inquiryId, Long ownerId) {
        Inquiry inquiry = inquiryRepository.findByIdWithProperty(inquiryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta no encontrada"));
        if (!ownerId.equals(inquiry.getProperty().getOwnerId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Consulta no encontrada");
        }
        return inquiry;
    }
}
