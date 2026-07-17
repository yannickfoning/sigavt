package com.sigavt.entity;

import com.sigavt.enums.Poste;
import com.sigavt.enums.StatutEmploye;
import com.sigavt.enums.TypeContrat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "personnel")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Personnel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_complet", nullable = false, length = 150)
    private String nomComplet;

    @Column(length = 30)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Poste poste;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_contrat", length = 10)
    private TypeContrat typeContrat;

    @Column(name = "date_fin_contrat")
    private LocalDate dateFinContrat;

    @Column(name = "salaire_base", precision = 10, scale = 2)
    private BigDecimal salaireBase;

    @Column(name = "numero_cnps", length = 30)
    private String numeroCnps;

    @Column(name = "numero_cni", length = 30)
    private String numeroCni;

    @Column(name = "permis_conduire", length = 10)
    private String permisConduire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_assigne_id")
    @JsonIgnore
    private Bus busAssigne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id")
    @JsonIgnore
    private Agence agence;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutEmploye statut = StatutEmploye.ACTIF;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;
}
