package com.sigavt.entity;

import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bareme_tarif_colis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BaremeTarifColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "poids_min", precision = 5, scale = 2, nullable = false)
    private BigDecimal poidsMin;

    @Column(name = "poids_max", precision = 5, scale = 2, nullable = false)
    private BigDecimal poidsMax;

    @Column(name = "tarif_base", precision = 10, scale = 2, nullable = false)
    private BigDecimal tarifBase;

    @Builder.Default
    @Column(nullable = false)
    private boolean actif = true;

    @Builder.Default
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_modification")
    private LocalDateTime dateModification;
}
