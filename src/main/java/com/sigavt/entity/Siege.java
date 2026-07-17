package com.sigavt.entity;

import com.sigavt.enums.StatutSiege;
import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "sieges")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Siege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voyage_id", nullable = false)
    private Voyage voyage;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private StatutSiege statut = StatutSiege.LIBRE;
}