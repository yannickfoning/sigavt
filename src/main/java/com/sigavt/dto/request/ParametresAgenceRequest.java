package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParametresAgenceRequest {
    @NotBlank private String nomAgence;
    private String telephone;
    private String email;
    private String villePrincipale;
    private String adresse;
}
