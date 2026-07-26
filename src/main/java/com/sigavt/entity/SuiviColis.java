package com.sigavt.entity;

import com.sigavt.enums.StatutColis;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "suivi_colis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SuiviColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colis_id", nullable = false)
    private Colis colis;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutColis statut;

    @Column(nullable = false, length = 255)
    private String evenement;

    @Column(length = 255)
    private String localisation;

    @Column(name = "date_evenement", nullable = false)
    private LocalDateTime dateEvenement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = true)
    private Utilisateur agent;
}