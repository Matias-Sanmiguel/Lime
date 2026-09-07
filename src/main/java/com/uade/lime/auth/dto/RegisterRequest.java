package com.uade.lime.auth.dto;

import com.uade.lime.auth.model.UserRole;
import com.uade.lime.auth.model.Sex;
import java.time.LocalDate;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 100) String name,
        UserRole role,
        @NotNull @Past LocalDate birthDate,
        @NotNull Sex sex,
        @Size(max = 150) String agencyName) {
}
