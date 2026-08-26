package com.uade.lime.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateMeRequest {
    @Size(max=100)
    @NotBlank
    private String name;
    @Size(max=100)
    private String agencyName;
    private String currentPassword;
    @Size(min=8)
    private String newPassword;

}
