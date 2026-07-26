package com.sigavt.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UtilisateurRequest {
    @NotBlank
    private String nomComplet;

    @NotBlank @Email
    private String email;

    @NotBlank
    private String motDePasse;

    @Pattern(regexp = "\\+237[0-9]{9}", message = "Le téléphone doit être au format camerounais: +237XXXXXXXXX")
    private String telephone;

    private String role; // ADMIN, GERANT, BILLETTERIE, ...

    private Long agenceId;
}
