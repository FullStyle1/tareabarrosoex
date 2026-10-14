package com.barroso.parcelservice.exception;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.barroso.parcelservice.dto.ParcelRequestDTO;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ExceptionTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void returnsDetailedBadRequestForInvalidDto() throws Exception {
        mockMvc.perform(post("/test/validation")
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
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Uno o más campos tienen valores inválidos."))
                .andExpect(jsonPath("$.path").value("/test/validation"))
                .andExpect(jsonPath("$.errors[*].field", hasItem("name")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("location")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("soilType")))
                .andExpect(jsonPath("$.errors[*].field", hasItem("ownerOrLead")));
    }

    @Test
    void returnsNotFoundForMissingResource() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("No se encontró la parcela solicitada."))
                .andExpect(jsonPath("$.path").value("/test/not-found"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    void returnsBadRequestForBusinessRuleFailure() throws Exception {
        mockMvc.perform(get("/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("La regla de negocio no se cumple."));
    }

    @Test
    void returnsGenericInternalErrorWithoutLeakingExceptionDetails() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message")
                        .value("Ocurrió un error interno. Intenta nuevamente más tarde."))
                .andExpect(jsonPath("$.path").value("/test/unexpected"))
                .andExpect(jsonPath("$.errors").isEmpty())
                .andExpect(content().string(not(containsString("Detalles internos que no deben exponerse."))));
    }

    @RestController
    static class ExceptionTestController {

        @PostMapping("/test/validation")
        void validate(@Valid @RequestBody ParcelRequestDTO request) {
        }

        @GetMapping("/test/not-found")
        void notFound() {
            throw new ResourceNotFoundException("No se encontró la parcela solicitada.");
        }

        @GetMapping("/test/bad-request")
        void badRequest() {
            throw new BadRequestException("La regla de negocio no se cumple.");
        }

        @GetMapping("/test/unexpected")
        void unexpected() {
            throw new IllegalStateException("Detalles internos que no deben exponerse.");
        }
    }
}
