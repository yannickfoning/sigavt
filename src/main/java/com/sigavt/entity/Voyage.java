package com.sigavt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigavt.enums.StatutVoyage;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "voyages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Voyage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ligne_id", nullable = false)
    @JsonIgnore
    private Ligne ligne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id")
    @JsonIgnore
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chauffeur_id")
    @JsonIgnore
    private Personnel chauffeur;

    @Column(name = "date_voyage", nullable = false)
    private LocalDate dateVoyage;

    @Column(name = "heure_depart", nullable = false)
    private LocalTime heureDepart;

    @Column(name = "places_disponibles", nullable = false)
    private Integer placesDisponibles;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutVoyage statut = StatutVoyage.PLANIFIE;
}
