package com.barroso.parcelservice.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorDto> errors
) {
}
