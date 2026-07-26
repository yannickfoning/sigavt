package com.sigavt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sigavt.enums.StatutSiege;
import com.sigavt.enums.ClasseSiege;
import javax.persistence.*;
import lombok.*;

import javax.persistence.PostLoad;

@Entity
@Table(name = "sieges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Siege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String numero;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voyage_id", nullable = false)
    @JsonIgnore
    private Voyage voyage;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutSiege statut = StatutSiege.LIBRE;

    @Column(name = "classe", length = 20)
    private String classeString;

    public ClasseSiege getClasse() {
        if (classeString == null || classeString.isEmpty()) {
            return ClasseSiege.CLASSIQUE;
        }
        try {
            return ClasseSiege.valueOf(classeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ClasseSiege.CLASSIQUE;
        }
    }

    public void setClasse(ClasseSiege classe) {
        this.classeString = classe != null ? classe.name() : ClasseSiege.CLASSIQUE.name();
    }
}