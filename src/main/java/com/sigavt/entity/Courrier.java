package com.sigavt.entity;

import com.sigavt.enums.StatutCourrier;
import com.sigavt.enums.TypeCourrier;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "courriers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Courrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_courrier", nullable = false, unique = true, length = 30)
    private String numeroCourrier;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_courrier", nullable = false, length = 20)
    private TypeCourrier typeCourrier;

    @Column(name = "type", length = 20)
    private String type;

    @Column(nullable = false, length = 255)
    private String objet;

    @Column(name = "expediteur", length = 150)
    private String expediteur;

    @Column(name = "destinataire", length = 150)
    private String destinataire;

    @Column(name = "date_reception", nullable = false)
    private LocalDate dateReception;

    @Column(name = "date_traitement")
    private LocalDate dateTraitement;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutCourrier statut = StatutCourrier.NON_LU;

    @Column(length = 1000)
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id")
    private Agence agence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_traitement_id")
    private Utilisateur agentTraitement;
}