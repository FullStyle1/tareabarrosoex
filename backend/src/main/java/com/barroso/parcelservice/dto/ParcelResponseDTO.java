package com.barroso.parcelservice.dto;

import com.barroso.parcelservice.model.SoilType;
import java.time.LocalDateTime;

public record ParcelResponseDTO(
        Long id,
        String name,
        String location,
        SoilType soilType,
        String ownerOrLead,
        LocalDateTime createdAt
) {
}
