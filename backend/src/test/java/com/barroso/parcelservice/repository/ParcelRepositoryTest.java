package com.barroso.parcelservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.barroso.parcelservice.model.Parcel;
import com.barroso.parcelservice.model.SoilType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ParcelRepositoryTest {

    @Autowired
    private ParcelRepository parcelRepository;

    @Test
    void persistsParcelAndAssignsCreatedAt() {
        Parcel parcel = new Parcel();
        parcel.setName("Huerto central");
        parcel.setLocation("19.4326, -99.1332");
        parcel.setSoilType(SoilType.ARCILLOSO);
        parcel.setOwnerOrLead("Ana Pérez");

        Parcel savedParcel = parcelRepository.saveAndFlush(parcel);

        assertNotNull(savedParcel.getId());
        assertNotNull(savedParcel.getCreatedAt());
        assertEquals(SoilType.ARCILLOSO, savedParcel.getSoilType());
        assertEquals("Huerto central", savedParcel.getName());
    }
}
