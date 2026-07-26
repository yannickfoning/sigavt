package com.sigavt.dto.response;

import com.sigavt.enums.StatutVoyage;
import com.sigavt.enums.ClasseSiege;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private Integer placesTotal;
    private BigDecimal tarifBase;
    private BigDecimal tarifClassique;
    private BigDecimal tarifVip;
    private BigDecimal tarifVvip;
    private StatutVoyage statut;
    private String busImmatriculation;
    private String chauffeurNom;
    private ClasseSiege classeBus;
}
