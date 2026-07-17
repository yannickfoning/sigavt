package com.sigavt.repository;

import com.sigavt.entity.Bus;
import com.sigavt.enums.StatutBus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusRepository extends JpaRepository<Bus, Long> {
    Optional<Bus> findByImmatriculation(String immatriculation);
    List<Bus> findByStatut(StatutBus statut);

    Page<Bus> findAll(Pageable pageable);
    Page<Bus> findByStatut(StatutBus statut, Pageable pageable);
}
