package com.sigavt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sigavt.enums.StatutVoyage;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "voyages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Voyage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ligne_id", nullable = false)
    @JsonIgnore
    private Ligne ligne;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bus_id")
    @JsonIgnore
    private Bus bus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chauffeur_id")
    @JsonIgnore
    private Personnel chauffeur;

    @OneToMany(mappedBy = "voyage", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private java.util.List<Siege> sieges = new java.util.ArrayList<>();

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
