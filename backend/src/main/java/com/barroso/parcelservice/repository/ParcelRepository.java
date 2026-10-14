package com.barroso.parcelservice.repository;

import com.barroso.parcelservice.model.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParcelRepository extends JpaRepository<Parcel, Long> {
}
