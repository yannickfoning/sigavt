package com.sigavt.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "parametres_agence")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParametresAgence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_agence", nullable = false, length = 150)
    private String nomAgence;

    @Column(length = 30)
    private String telephone;

    @Column(length = 120)
    private String email;

    @Column(name = "ville_principale", length = 80)
    private String villePrincipale;

    @Column(length = 255)
    private String adresse;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;
}