package com.sigavt.entity;

import com.sigavt.enums.StatutLigne;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "lignes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ligne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ville_depart", nullable = false, length = 80)
    private String villeDepart;

    @Column(name = "ville_arrivee", nullable = false, length = 80)
    private String villeArrivee;

    @Column(name = "distance_km")
    private Integer distanceKm;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    @Column(name = "tarif_base", precision = 10, scale = 2, nullable = false)
    private BigDecimal tarifBase;

    @Column(name = "frequence_jour")
    private Integer frequenceJour;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutLigne statut = StatutLigne.ACTIVE;
}
