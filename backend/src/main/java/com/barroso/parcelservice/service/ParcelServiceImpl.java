package com.barroso.parcelservice.service;

import com.barroso.parcelservice.dto.ParcelRequestDTO;
import com.barroso.parcelservice.dto.ParcelResponseDTO;
import com.barroso.parcelservice.exception.ResourceNotFoundException;
import com.barroso.parcelservice.model.Parcel;
import com.barroso.parcelservice.repository.ParcelRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParcelServiceImpl implements ParcelService {

    private final ParcelRepository parcelRepository;

    public ParcelServiceImpl(ParcelRepository parcelRepository) {
        this.parcelRepository = parcelRepository;
    }

    @Override
    public ParcelResponseDTO createParcel(ParcelRequestDTO dto) {
        Parcel parcel = new Parcel();
        parcel.setName(dto.getName());
        parcel.setLocation(dto.getLocation());
        parcel.setSoilType(dto.getSoilType());
        parcel.setOwnerOrLead(dto.getOwnerOrLead());

        return toResponseDTO(parcelRepository.save(parcel));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelResponseDTO> getAllParcels() {
        return parcelRepository.findAll()
                .stream()
                .map(ParcelServiceImpl::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ParcelResponseDTO getParcelById(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la parcela con ID " + id + "."));

        return toResponseDTO(parcel);
    }

    private static ParcelResponseDTO toResponseDTO(Parcel parcel) {
        return new ParcelResponseDTO(
                parcel.getId(),
                parcel.getName(),
                parcel.getLocation(),
                parcel.getSoilType(),
                parcel.getOwnerOrLead(),
                parcel.getCreatedAt()
        );
    }
}
