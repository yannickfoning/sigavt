package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ColisRequest {
    @NotBlank private String expediteurNom;
    @Pattern(regexp = "\\+237[0-9]{9}", message = "Le téléphone doit être au format camerounais: +237XXXXXXXXX")
    private String expediteurTelephone;
    @NotBlank private String destinataireNom;
    @Pattern(regexp = "\\+237[0-9]{9}", message = "Le téléphone doit être au format camerounais: +237XXXXXXXXX")
    private String destinataireTelephone;
    @NotBlank private String villeDepart;
    @NotBlank private String villeArrivee;
    @NotNull @Positive private BigDecimal poidsKg;
    private String typeColis;
    private String description;
    private boolean fragile;
    private boolean urgent;
    private boolean assure;
    @NotBlank private String modePaiement;
    private Long voyageId; // Optional: assign to specific voyage
}
