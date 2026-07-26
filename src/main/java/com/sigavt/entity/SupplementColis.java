package com.sigavt.entity;

import com.sigavt.enums.TypeSupplementColis;
import javax.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplement_colis")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupplementColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_supplement", nullable = false, unique = true, length = 20)
    private TypeSupplementColis typeSupplement;

    @Column(name = "montant", precision = 10, scale = 2, nullable = false)
    private BigDecimal montant;

    @Builder.Default
    @Column(nullable = false)
    private boolean actif = true;

    @Builder.Default
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
}
