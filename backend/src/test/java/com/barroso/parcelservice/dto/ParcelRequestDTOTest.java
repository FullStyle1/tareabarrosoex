package com.barroso.parcelservice.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.barroso.parcelservice.model.SoilType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ParcelRequestDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    @Test
    void sanitizesAndTrimsTextFields() {
        ParcelRequestDTO request = new ParcelRequestDTO();

        request.setName("  <b>Huerto</b><script>alert('x')</script> comunitario  ");
        request.setLocation(" 19.4326, -99.1332 ");
        request.setOwnerOrLead("  <i>Ana</i> Pérez  ");

        assertEquals("Huerto comunitario", request.getName());
        assertEquals("19.4326, -99.1332", request.getLocation());
        assertEquals("Ana Pérez", request.getOwnerOrLead());
    }

    @Test
    void acceptsValidParcelRequest() {
        ParcelRequestDTO request = validRequest();

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsInvalidCoordinatesAndMissingSoilType() {
        ParcelRequestDTO request = validRequest();
        request.setLocation("91, -181");
        request.setSoilType(null);

        Set<String> invalidProperties = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertFalse(invalidProperties.contains("name"));
        assertTrue(invalidProperties.contains("location"));
        assertTrue(invalidProperties.contains("soilType"));
    }

    private static ParcelRequestDTO validRequest() {
        ParcelRequestDTO request = new ParcelRequestDTO();
        request.setName("Huerto central");
        request.setLocation("19.4326, -99.1332");
        request.setSoilType(SoilType.ARCILLOSO);
        request.setOwnerOrLead("Ana Pérez");
        return request;
    }
}
