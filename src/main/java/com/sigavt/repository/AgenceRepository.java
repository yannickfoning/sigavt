package com.sigavt.repository;

import com.sigavt.entity.Agence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgenceRepository extends JpaRepository<Agence, Long> {
    @Query("SELECT a FROM Agence a WHERE LOWER(a.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.ville) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Agence> searchAgences(String search, Pageable pageable);
}
