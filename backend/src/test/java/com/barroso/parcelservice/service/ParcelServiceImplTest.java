package com.barroso.parcelservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barroso.parcelservice.dto.ParcelRequestDTO;
import com.barroso.parcelservice.dto.ParcelResponseDTO;
import com.barroso.parcelservice.exception.ResourceNotFoundException;
import com.barroso.parcelservice.model.Parcel;
import com.barroso.parcelservice.model.SoilType;
import com.barroso.parcelservice.repository.ParcelRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParcelServiceImplTest {

    @Mock
    private ParcelRepository parcelRepository;

    @InjectMocks
    private ParcelServiceImpl parcelService;

    private Parcel parcel;

    @BeforeEach
    void setUp() {
        parcel = new Parcel();
        parcel.setId(42L);
        parcel.setName("Huerto central");
        parcel.setLocation("19.4326, -99.1332");
        parcel.setSoilType(SoilType.ARCILLOSO);
        parcel.setOwnerOrLead("Ana Pérez");
        parcel.setCreatedAt(LocalDateTime.of(2026, 10, 14, 17, 0));
    }

    @Test
    void createsParcelAndMapsTheSavedEntityToResponse() {
        ParcelRequestDTO request = request();
        when(parcelRepository.save(org.mockito.ArgumentMatchers.any(Parcel.class))).thenReturn(parcel);

        ParcelResponseDTO response = parcelService.createParcel(request);

        assertEquals(42L, response.id());
        assertEquals("Huerto central", response.name());
        assertEquals("19.4326, -99.1332", response.location());
        assertEquals(SoilType.ARCILLOSO, response.soilType());
        assertEquals("Ana Pérez", response.ownerOrLead());
        assertNotNull(response.createdAt());
        verify(parcelRepository).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.getName().equals(request.getName())
                        && saved.getLocation().equals(request.getLocation())
                        && saved.getSoilType() == request.getSoilType()
                        && saved.getOwnerOrLead().equals(request.getOwnerOrLead())));
    }

    @Test
    void returnsAllParcelsMappedToResponses() {
        when(parcelRepository.findAll()).thenReturn(List.of(parcel));

        List<ParcelResponseDTO> responses = parcelService.getAllParcels();

        assertEquals(1, responses.size());
        assertEquals(42L, responses.get(0).id());
        assertEquals("Huerto central", responses.get(0).name());
    }

    @Test
    void throwsNotFoundWhenParcelDoesNotExist() {
        when(parcelRepository.findById(404L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> parcelService.getParcelById(404L)
        );

        assertEquals("No se encontró la parcela con ID 404.", exception.getMessage());
    }

    @Test
    void returnsParcelByIdMappedToResponse() {
        when(parcelRepository.findById(42L)).thenReturn(Optional.of(parcel));

        ParcelResponseDTO response = parcelService.getParcelById(42L);

        assertEquals(42L, response.id());
        assertEquals("Huerto central", response.name());
        assertEquals("Ana Pérez", response.ownerOrLead());
    }

    private static ParcelRequestDTO request() {
        ParcelRequestDTO request = new ParcelRequestDTO();
        request.setName("Huerto central");
        request.setLocation("19.4326, -99.1332");
        request.setSoilType(SoilType.ARCILLOSO);
        request.setOwnerOrLead("Ana Pérez");
        return request;
    }
}
