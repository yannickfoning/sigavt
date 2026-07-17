package com.sigavt.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class VoyageRequest {
    @NotNull private Long ligneId;
    private Long busId;
    private Long chauffeurId;
    @NotNull private LocalDate dateVoyage;
    @NotNull private LocalTime heureDepart;
    private Integer placesDisponibles;
    private String statut;
}
