package com.sigavt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigavt.enums.StatutPaiement;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bulletins_paie")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BulletinPaie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personnel_id", nullable = false)
    @JsonIgnore
    private Personnel personnel;

    @Column(name = "periode", nullable = false, length = 20)
    private String periode;

    @Column(name = "salaire_base", precision = 10, scale = 2)
    private BigDecimal salaireBase;

    @Column(name = "taux_horaire_sup", precision = 10, scale = 2)
    private BigDecimal tauxHoraireSup;

    @Column(name = "prime_anciennete", precision = 10, scale = 2)
    private BigDecimal primeAnciennete;

    @Column(name = "prime_performance", precision = 10, scale = 2)
    private BigDecimal primePerformance;

    @Column(name = "irpp", precision = 10, scale = 2)
    private BigDecimal irpp;

    @Column(name = "autres_retenues", precision = 10, scale = 2)
    private BigDecimal autresRetenues;

    @Column(name = "net_a_payer", precision = 10, scale = 2)
    private BigDecimal netAPayer;

    @Column(name = "indemnite_transport", precision = 10, scale = 2)
    private BigDecimal indemniteTransport;

    @Column(name = "heures_supplementaires", precision = 5, scale = 2)
    private BigDecimal heuresSupplementaires;

    @Column(name = "montant_heures_supp", precision = 10, scale = 2)
    private BigDecimal montantHeuresSupp;

    @Column(name = "salaire_brut", precision = 10, scale = 2)
    private BigDecimal salaireBrut;

    @Column(name = "cotisation_cnps", precision = 10, scale = 2)
    private BigDecimal cotisationCnps;

    @Column(name = "retenue_irpp", precision = 10, scale = 2)
    private BigDecimal retenueIrpp;

    @Column(name = "avance_salaire", precision = 10, scale = 2)
    private BigDecimal avanceSalaire;

    @Column(name = "charges_patronales", precision = 10, scale = 2)
    private BigDecimal chargesPatronales;

    @Column(name = "cout_employeur", precision = 10, scale = 2)
    private BigDecimal coutEmployeur;

    @Column(name = "mode_reglement", length = 20)
    private String modeReglement;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutPaiement statut = StatutPaiement.EN_ATTENTE;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Column(name = "date_generation", nullable = false)
    private LocalDate dateGeneration;
}