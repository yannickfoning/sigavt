package com.sigavt.repository;

import com.sigavt.entity.Siege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiegeRepository extends JpaRepository<Siege, Long> {
    List<Siege> findByVoyage_Id(Long voyageId);
}
