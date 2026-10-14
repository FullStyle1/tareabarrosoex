package com.barroso.parcelservice.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barroso.parcelservice.dto.ParcelRequestDTO;
import com.barroso.parcelservice.dto.ParcelResponseDTO;
import com.barroso.parcelservice.exception.GlobalExceptionHandler;
import com.barroso.parcelservice.exception.ResourceNotFoundException;
import com.barroso.parcelservice.model.SoilType;
import com.barroso.parcelservice.service.ParcelService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ParcelController.class)
@Import(GlobalExceptionHandler.class)
class ParcelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParcelService parcelService;

    @Test
    void createsParcelAndReturnsCreatedLocation() throws Exception {
        when(parcelService.createParcel(any(ParcelRequestDTO.class))).thenReturn(parcelResponse());

        mockMvc.perform(post("/api/v1/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Huerto central",
                                  "location": "19.4326, -99.1332",
                                  "soilType": "ARCILLOSO",
                                  "ownerOrLead": "Ana Pérez"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/parcels/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Huerto central"))
                .andExpect(jsonPath("$.soilType").value("ARCILLOSO"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void rejectsInvalidParcelRequestWithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/v1/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "location": "91, -181",
                                  "soilType": null,
                                  "ownerOrLead": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[*].field", hasItem("name")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("location")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("soilType")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("ownerOrLead")));
    }

    @Test
    void listsParcels() throws Exception {
        when(parcelService.getAllParcels()).thenReturn(List.of(parcelResponse()));

        mockMvc.perform(get("/api/v1/parcels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(42))
                .andExpect(jsonPath("$[0].name").value("Huerto central"))
                .andExpect(jsonPath("$[0].location").value("19.4326, -99.1332"))
                .andExpect(jsonPath("$[0].soilType").value("ARCILLOSO"));
    }

    @Test
    void returnsParcelById() throws Exception {
        when(parcelService.getParcelById(42L)).thenReturn(parcelResponse());

        mockMvc.perform(get("/api/v1/parcels/{id}", 42))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.name").value("Huerto central"));
    }

    @Test
    void returnsNotFoundResponseWhenParcelIdDoesNotExist() throws Exception {
        when(parcelService.getParcelById(eq(404L)))
                .thenThrow(new ResourceNotFoundException("No se encontró la parcela con ID 404."));

        mockMvc.perform(get("/api/v1/parcels/{id}", 404))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró la parcela con ID 404."))
                .andExpect(jsonPath("$.path").value("/api/v1/parcels/404"));
    }

    private static ParcelResponseDTO parcelResponse() {
        return new ParcelResponseDTO(
                42L,
                "Huerto central",
                "19.4326, -99.1332",
                SoilType.ARCILLOSO,
                "Ana Pérez",
                LocalDateTime.of(2026, 10, 14, 17, 0)
        );
    }
}
