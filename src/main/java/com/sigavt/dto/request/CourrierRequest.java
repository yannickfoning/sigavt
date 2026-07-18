package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CourrierRequest {
    private String type; // ENTRANT, SORTANT
    private String objet;
    private String expediteur;
    private String destinataire;
    private String contenu;
    private String statut;
    private LocalDate dateReception;
    private LocalDate dateTraitement;
    private String observations;
}
