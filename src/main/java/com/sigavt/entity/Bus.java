package com.sigavt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sigavt.enums.StatutBus;
import com.sigavt.enums.ClasseSiege;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDate;
import javax.persistence.PostLoad;

@Entity
@Table(name = "bus")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @ManyToOne(fetch = FetchType.EAGER)
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

    @Column(name = "classe_bus", length = 20)
    private String classeBusString;

    public ClasseSiege getClasseBus() {
        if (classeBusString == null || classeBusString.isEmpty()) {
            return ClasseSiege.CLASSIQUE;
        }
        try {
            return ClasseSiege.valueOf(classeBusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ClasseSiege.CLASSIQUE;
        }
    }

    public void setClasseBus(ClasseSiege classeBus) {
        this.classeBusString = classeBus != null ? classeBus.name() : ClasseSiege.CLASSIQUE.name();
    }
}
