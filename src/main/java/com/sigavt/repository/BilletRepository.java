package com.sigavt.repository;

import com.sigavt.entity.Billet;
import com.sigavt.enums.StatutBillet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BilletRepository extends JpaRepository<Billet, Long> {
    Optional<Billet> findByNumeroBillet(String numeroBillet);
    List<Billet> findByVoyage_Id(Long voyageId);
    long countByVoyage_Id(Long voyageId);
    List<Billet> findByDateEmissionBetween(LocalDateTime start, LocalDateTime end);

    Page<Billet> findAll(Pageable pageable);
    Page<Billet> findByStatut(StatutBillet statut, Pageable pageable);
    Page<Billet> findByPassagerNomContainingIgnoreCase(String nom, Pageable pageable);
}
