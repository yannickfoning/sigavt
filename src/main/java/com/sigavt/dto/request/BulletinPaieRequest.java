package com.sigavt.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BulletinPaieRequest {
    @NotNull private Long personnelId;
    @NotBlank private String periode; // yyyy-MM
    private BigDecimal indemniteTransport;
    private BigDecimal primeAnciennete;
    private BigDecimal primePerformance;
    private BigDecimal heuresSup;
    private BigDecimal tauxHoraireSup;
    private BigDecimal autresRetenues;
}
