package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BilletRequest {
    @NotNull private Long voyageId;
    private Long siegeId;
    private String numeroSiege; // alternative : numero libre (ex "1B"), cree/assigne automatiquement
    @NotBlank private String passagerNom;
    @Pattern(regexp = "\\+237[0-9]{9}", message = "Le téléphone doit être au format camerounais: +237XXXXXXXXX")
    private String passagerTelephone;
    @NotBlank private String typeTarif;
    @NotBlank private String modePaiement;
    
    // Assouplir la validation du téléphone pour accepter les formats avec espaces
    public void setPassagerTelephone(String passagerTelephone) {
        this.passagerTelephone = passagerTelephone != null ? passagerTelephone.replaceAll("\\s", "") : null;
    }
}
