package com.sigavt.entity;

import com.sigavt.enums.ModePaiement;
import com.sigavt.enums.StatutBillet;
import com.sigavt.enums.TypeTarif;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_billet", nullable = false, unique = true, length = 30)
    private String numeroBillet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voyage_id", nullable = false)
    private Voyage voyage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "siege_id")
    private Siege siege;

    @Column(name = "passager_nom", nullable = false, length = 150)
    private String passagerNom;

    @Column(name = "passager_telephone", length = 30)
    private String passagerTelephone;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_tarif", nullable = false, length = 30)
    private TypeTarif typeTarif;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false, length = 20)
    private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutBillet statut = StatutBillet.VALIDE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_vente_id")
    private Utilisateur agentVente;

    @Builder.Default
    @Column(name = "date_emission", nullable = false)
    private LocalDateTime dateEmission = LocalDateTime.now();
}
