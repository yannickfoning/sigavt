package com.sigavt.repository;

import com.sigavt.entity.Voyage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VoyageRepository extends JpaRepository<Voyage, Long> {
    List<Voyage> findByDateVoyage(LocalDate dateVoyage);
    List<Voyage> findByLigne_IdAndDateVoyage(Long ligneId, LocalDate dateVoyage);
    List<Voyage> findByLigne_Id(Long ligneId);
    List<Voyage> findByBus_Id(Long busId);

    Page<Voyage> findAll(Pageable pageable);
    Page<Voyage> findByDateVoyage(LocalDate dateVoyage, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM Voyage v LEFT JOIN FETCH v.ligne WHERE v.id = :id")
    Optional<Voyage> findByIdWithLock(@Param("id") Long id);
}
