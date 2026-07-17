package com.sigavt.dto.response;

import com.sigavt.enums.StatutVoyage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoyageResponse {
    private Long id;
    private String villeDepart;
    private String villeArrivee;
    private LocalDate dateVoyage;
    private LocalTime heureDepart;
    private Integer placesDisponibles;
    private StatutVoyage statut;
    private String busImmatriculation;
    private String chauffeurNom;
}
