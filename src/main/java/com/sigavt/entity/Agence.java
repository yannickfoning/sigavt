package com.sigavt.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "agences")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(nullable = false, length = 80)
    private String ville;

    @Column(length = 255)
    private String adresse;

    @Column(length = 30)
    private String telephone;

    @Column(length = 120)
    private String email;
}
