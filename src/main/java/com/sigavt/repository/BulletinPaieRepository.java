package com.sigavt.repository;

import com.sigavt.entity.BulletinPaie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BulletinPaieRepository extends JpaRepository<BulletinPaie, Long> {
    List<BulletinPaie> findByPeriode(String periode);
    Optional<BulletinPaie> findByPersonnel_IdAndPeriode(Long personnelId, String periode);
}
