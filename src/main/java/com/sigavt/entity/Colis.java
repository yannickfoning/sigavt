package com.sigavt.entity;

import com.sigavt.enums.ModePaiement;
import com.sigavt.enums.StatutColis;
import com.sigavt.enums.TypeColis;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "colis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Colis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_tracking", nullable = false, unique = true, length = 30)
    private String numeroTracking;

    @Column(name = "expediteur_nom", nullable = false, length = 150)
    private String expediteurNom;

    @Column(name = "expediteur_tel", length = 30)
    private String expediteurTel;

    @Column(name = "destinataire_nom", nullable = false, length = 150)
    private String destinataireNom;

    @Column(name = "destinataire_tel", length = 30)
    private String destinataireTel;

    @Column(name = "ville_depart", nullable = false, length = 100)
    private String villeDepart;

    @Column(name = "ville_arrivee", nullable = false, length = 100)
    private String villeArrivee;

    @Column(name = "poids_kg", precision = 5, scale = 2)
    private BigDecimal poidsKg;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_colis", length = 20)
    private TypeColis typeColis;

    @Column(nullable = false)
    @Builder.Default
    private boolean fragile = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean urgent = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean assure = false;

    @Column(precision = 10, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", length = 20)
    private ModePaiement modePaiement;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutColis statut = StatutColis.ENREGISTRE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id")
    private Agence agence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Utilisateur agent;

    @Builder.Default
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_livraison")
    private LocalDateTime dateLivraison;
}