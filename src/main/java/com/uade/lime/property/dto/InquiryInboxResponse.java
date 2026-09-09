package com.uade.lime.property.dto;

import org.springframework.data.domain.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor 
public class InquiryInboxResponse {
    private Page<InquiryResponse> inquiries;
    private long unreadCount;

}
