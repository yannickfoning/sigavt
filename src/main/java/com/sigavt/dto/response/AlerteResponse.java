package com.sigavt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AlerteResponse {
    private String type;    // VISITE_TECHNIQUE, COLIS_NON_RECLAME, ASSURANCE
    private String titre;
    private String details;
    private String niveau;  // ROUGE, ORANGE, BLEU
}
