package com.sigavt.entity;

import com.sigavt.enums.CategorieConfiguration;
import javax.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "configuration_metier")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConfigurationMetier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cle", nullable = false, unique = true, length = 50)
    private String cle;

    @Column(nullable = false, length = 255)
    private String valeur;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "categorie", nullable = false, length = 30)
    private CategorieConfiguration categorie;

    @Builder.Default
    @Column(name = "date_modification", nullable = false)
    private LocalDateTime dateModification = LocalDateTime.now();
}
