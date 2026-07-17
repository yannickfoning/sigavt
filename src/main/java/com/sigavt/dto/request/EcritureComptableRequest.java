package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EcritureComptableRequest {
    @NotNull private LocalDate dateEcriture;
    @NotBlank private String libelle;
    private String description;
    private String categorie;
    private String typeEcriture;
    private String compteDebit;
    private String compteCredit;
    private BigDecimal montantDebit;
    private BigDecimal montantCredit;
    private BigDecimal debit;
    private BigDecimal credit;
    private String reference;
}
