package com.sigavt.repository;

import com.sigavt.entity.SuiviColis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuiviColisRepository extends JpaRepository<SuiviColis, Long> {
    List<SuiviColis> findByColis_IdOrderByDateEvenementDesc(Long colisId);
}
