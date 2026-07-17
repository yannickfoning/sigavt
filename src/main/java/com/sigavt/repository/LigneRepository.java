package com.sigavt.repository;

import com.sigavt.entity.Ligne;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LigneRepository extends JpaRepository<Ligne, Long> {
    @Query("SELECT l FROM Ligne l WHERE LOWER(l.villeDepart) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(l.villeArrivee) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Ligne> searchLignes(String search, Pageable pageable);
}
