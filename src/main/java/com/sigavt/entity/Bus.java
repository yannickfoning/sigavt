package com.sigavt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigavt.enums.StatutBus;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "bus")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String immatriculation;

    @Column(length = 100)
    private String modele;

    @Column(name = "nombre_places", nullable = false)
    private Integer nombrePlaces;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ligne_assignee_id")
    @JsonIgnore
    private Ligne ligneAssignee;

    @Column(name = "prochain_entretien")
    private LocalDate prochainEntretien;

    @Column(name = "assurance_expiration")
    private LocalDate assuranceExpiration;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutBus statut = StatutBus.OPERATIONNEL;
}
