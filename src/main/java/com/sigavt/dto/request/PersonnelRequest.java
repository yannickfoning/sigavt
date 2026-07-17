package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PersonnelRequest {
    @NotBlank private String nomComplet;
    private String telephone;
    @NotBlank private String poste;
    private String typeContrat;
    private LocalDate dateFinContrat;
    private BigDecimal salaireBase;
    private String numeroCnps;
    private String numeroCni;
    private String permisConduire;
    private Long busAssigneId;
    private Long agenceId;
    private String statut;
    private LocalDate dateEmbauche;
}
