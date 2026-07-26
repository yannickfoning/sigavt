package com.sigavt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {
    private long voyagesAujourdHui;
    private long busEnService;
    private long busTotal;
    private long colisEnTransit;
    private long billetsVendusAujourdHui;
    private BigDecimal recettesAujourdHui;
    private List<AlerteResponse> alertes;
    private VoyageResponse voyageDuJour;
    private VoyageResponse prochainVoyage;
    private List<VoyageResponse> departsDuJour;
}
