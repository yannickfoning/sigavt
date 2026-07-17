package com.sigavt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SuiviColisResponse {
    private String numeroTracking;
    private String statutActuel;
    private String expediteurNom;
    private String destinataireNom;
    private String villeDepart;
    private String villeArrivee;
    private String livreur;
    private java.util.List<Etape> etapes;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Etape {
        private String statut;
        private String description;
        private LocalDateTime dateHeure;
    }
}
