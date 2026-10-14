package com.barroso.parcelservice.service;

import com.barroso.parcelservice.dto.ParcelRequestDTO;
import com.barroso.parcelservice.dto.ParcelResponseDTO;
import java.util.List;

public interface ParcelService {

    ParcelResponseDTO createParcel(ParcelRequestDTO dto);

    List<ParcelResponseDTO> getAllParcels();

    ParcelResponseDTO getParcelById(Long id);
}
