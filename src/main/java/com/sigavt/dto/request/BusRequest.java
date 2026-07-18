package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BusRequest {
    private String immatriculation;
    private String modele;
    private Integer nombrePlaces;
    private Long ligneAssigneeId;
    private LocalDate prochainEntretien;
    private LocalDate assuranceExpiration;
    private String statut;
}
