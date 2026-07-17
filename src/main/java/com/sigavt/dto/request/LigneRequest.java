package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LigneRequest {
    @NotBlank private String villeDepart;
    @NotBlank private String villeArrivee;
    private Integer distanceKm;
    private Integer dureeMinutes;
    @NotNull @Positive private BigDecimal tarifBase;
    private Integer frequenceJour;
    private String statut;
}
