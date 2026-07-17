package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CourrierRequest {
    @NotBlank private String type; // ENTRANT, SORTANT
    @NotBlank private String objet;
    private String expediteur;
    private String destinataire;
    private String contenu;
    private String statut;
    private LocalDate dateReception;
    private LocalDate dateTraitement;
    private String observations;
}
