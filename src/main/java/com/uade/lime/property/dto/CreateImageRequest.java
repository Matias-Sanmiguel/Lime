package com.uade.lime.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateImageRequest(@NotBlank @Size(max = 500) String url) {}
//valida que no venga un null y limita el largo
