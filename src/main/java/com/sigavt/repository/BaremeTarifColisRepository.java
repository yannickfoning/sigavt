package com.sigavt.repository;

import com.sigavt.entity.BaremeTarifColis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BaremeTarifColisRepository extends JpaRepository<BaremeTarifColis, Long> {

    List<BaremeTarifColis> findByActifTrueOrderByPoidsMinAsc();

    @Query("SELECT b FROM BaremeTarifColis b WHERE b.actif = true AND :poids >= b.poidsMin AND :poids <= b.poidsMax")
    Optional<BaremeTarifColis> findByPoids(@Param("poids") BigDecimal poids);
}
