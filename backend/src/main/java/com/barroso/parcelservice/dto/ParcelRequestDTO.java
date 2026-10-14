package com.barroso.parcelservice.dto;

import com.barroso.parcelservice.model.SoilType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class ParcelRequestDTO {

    @NotBlank(message = "El nombre de la parcela es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
    private String name;

    @NotBlank(message = "La ubicación es obligatoria.")
    @Pattern(
            regexp = "^-?(?:[0-8]?\\d(?:\\.\\d+)?|90(?:\\.0+)?),\\s*-?(?:(?:1[0-7]\\d|[1-9]?\\d)(?:\\.\\d+)?|180(?:\\.0+)?)$",
            message = "La ubicación debe usar coordenadas GPS válidas con formato latitud, longitud."
    )
    private String location;

    @NotNull(message = "El tipo de suelo es obligatorio.")
    private SoilType soilType;

    @NotBlank(message = "El responsable de la parcela es obligatorio.")
    @Size(max = 100, message = "El responsable no puede superar los 100 caracteres.")
    private String ownerOrLead;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = sanitize(name);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = sanitize(location);
    }

    public SoilType getSoilType() {
        return soilType;
    }

    public void setSoilType(SoilType soilType) {
        this.soilType = soilType;
    }

    public String getOwnerOrLead() {
        return ownerOrLead;
    }

    public void setOwnerOrLead(String ownerOrLead) {
        this.ownerOrLead = sanitize(ownerOrLead);
    }

    private static String sanitize(String value) {
        if (value == null) {
            return null;
        }

        return Jsoup.clean(value, Safelist.none()).trim();
    }
}
