package com.sigavt.repository;

import com.sigavt.entity.Courrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourrierRepository extends JpaRepository<Courrier, Long> {
    List<Courrier> findByAgence_Id(Long agenceId);
}
