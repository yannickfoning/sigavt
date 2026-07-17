package com.sigavt.repository;

import com.sigavt.entity.EcritureComptable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EcritureComptableRepository extends JpaRepository<EcritureComptable, Long> {
    List<EcritureComptable> findByDateEcritureBetweenOrderByDateEcritureDesc(LocalDate debut, LocalDate fin);
}
