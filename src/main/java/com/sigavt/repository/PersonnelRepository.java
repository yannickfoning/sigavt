package com.sigavt.repository;

import com.sigavt.entity.Personnel;
import com.sigavt.enums.Poste;
import com.sigavt.enums.StatutEmploye;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    List<Personnel> findByPoste(Poste poste);
    List<Personnel> findByStatut(StatutEmploye statut);
    List<Personnel> findByNomCompletContainingIgnoreCase(String nom);

    Page<Personnel> findAll(Pageable pageable);
    Page<Personnel> findByPoste(Poste poste, Pageable pageable);
    Page<Personnel> findByStatut(StatutEmploye statut, Pageable pageable);
    Page<Personnel> findByNomCompletContainingIgnoreCase(String nom, Pageable pageable);
}
