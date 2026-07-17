package com.sigavt.repository;

import com.sigavt.entity.Colis;
import com.sigavt.enums.StatutColis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColisRepository extends JpaRepository<Colis, Long> {
    Optional<Colis> findByNumeroTracking(String numeroTracking);
    List<Colis> findByStatut(StatutColis statut);

    Page<Colis> findAll(Pageable pageable);
    Page<Colis> findByStatut(StatutColis statut, Pageable pageable);
}
