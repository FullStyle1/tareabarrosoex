package com.barroso.parcelservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.barroso.parcelservice.dto.ErrorResponseDto;
import com.barroso.parcelservice.dto.ParcelResponseDTO;
import com.barroso.parcelservice.model.Parcel;
import com.barroso.parcelservice.model.SoilType;
import com.barroso.parcelservice.repository.ParcelRepository;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:parcel_api_test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver"
})
class ParcelApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ParcelRepository parcelRepository;

    @BeforeEach
    void clearParcels() {
        parcelRepository.deleteAll();
    }

    @Test
    void createsParcelAndReturns201WithLocation() {
        ResponseEntity<ParcelResponseDTO> response = postJson(
                validParcelJson(),
                ParcelResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Huerto central");
        assertThat(response.getBody().createdAt()).isNotNull();
        assertThat(response.getHeaders().getLocation())
                .isEqualTo(URI.create(parcelsUrl() + "/" + response.getBody().id()));
    }

    @Test
    void returns400WithFieldErrorsForInvalidParcel() {
        String invalidJson = """
                {
                  "name": "",
                  "location": "91, -181",
                  "soilType": null,
                  "ownerOrLead": ""
                }
                """;

        ResponseEntity<ErrorResponseDto> response = postJson(
                invalidJson,
                ErrorResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().errors())
                .extracting("field")
                .contains("name", "location", "soilType", "ownerOrLead");
    }

    @Test
    void returns400WithHelpfulMessageForInvalidSoilType() {
        String invalidJson = """
                {
                  "name": "Huerto central",
                  "location": "19.4326, -99.1332",
                  "soilType": "MIXTO",
                  "ownerOrLead": "Ana Pérez"
                }
                """;

        ResponseEntity<ErrorResponseDto> response = postJson(
                invalidJson,
                ErrorResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo(
                "El cuerpo de la solicitud no tiene un formato válido."
        );
        assertThat(response.getBody().path()).isEqualTo("/api/v1/parcels");
    }

    @Test
    void listsPersistedParcels() {
        saveParcel();

        ResponseEntity<ParcelResponseDTO[]> response = restTemplate.getForEntity(
                parcelsUrl(),
                ParcelResponseDTO[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].name()).isEqualTo("Huerto central");
    }

    @Test
    void returnsParcelById() {
        Parcel parcel = saveParcel();

        ResponseEntity<ParcelResponseDTO> response = restTemplate.getForEntity(
                parcelsUrl() + "/" + parcel.getId(),
                ParcelResponseDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo(parcel.getId());
    }

    @Test
    void returns404ForMissingParcel() {
        ResponseEntity<ErrorResponseDto> response = restTemplate.getForEntity(
                parcelsUrl() + "/999",
                ErrorResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("No se encontró la parcela con ID 999.");
    }

    private Parcel saveParcel() {
        Parcel parcel = new Parcel();
        parcel.setName("Huerto central");
        parcel.setLocation("19.4326, -99.1332");
        parcel.setSoilType(SoilType.ARCILLOSO);
        parcel.setOwnerOrLead("Ana Pérez");
        return parcelRepository.saveAndFlush(parcel);
    }

    private String parcelsUrl() {
        return "http://localhost:" + port + "/api/v1/parcels";
    }

    private <T> ResponseEntity<T> postJson(String json, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return restTemplate.postForEntity(
                parcelsUrl(),
                new HttpEntity<>(json, headers),
                responseType
        );
    }

    private static String validParcelJson() {
        return """
                {
                  "name": "Huerto central",
                  "location": "19.4326, -99.1332",
                  "soilType": "ARCILLOSO",
                  "ownerOrLead": "Ana Pérez"
                }
                """;
    }
}
