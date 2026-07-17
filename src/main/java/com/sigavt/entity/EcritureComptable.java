package com.sigavt.entity;

import com.sigavt.enums.TypeEcriture;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ecritures_comptables")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EcritureComptable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_ecriture", nullable = false, unique = true, length = 30)
    private String numeroEcriture;

    @Column(name = "date_ecriture", nullable = false)
    private LocalDate dateEcriture;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_ecriture", nullable = false, length = 20)
    private TypeEcriture typeEcriture;

    @Column(nullable = false, length = 150)
    private String libelle;

    @Column(length = 255)
    private String description;

    @Column(name = "categorie", length = 20)
    private String categorie;

    @Column(name = "compte_debit", length = 30)
    private String compteDebit;

    @Column(name = "compte_credit", length = 30)
    private String compteCredit;

    @Column(precision = 12, scale = 2)
    private BigDecimal montantDebit;

    @Column(precision = 12, scale = 2)
    private BigDecimal montantCredit;

    @Column(name = "debit", precision = 12, scale = 2)
    private BigDecimal debit;

    @Column(name = "credit", precision = 12, scale = 2)
    private BigDecimal credit;

    @Column(length = 255)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voyage_id")
    private Voyage voyage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billet_id")
    private Billet billet;
}