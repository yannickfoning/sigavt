package com.sigavt.repository;

import com.sigavt.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByAgence_Id(Long agenceId);
    boolean existsByEmail(String email);
}
