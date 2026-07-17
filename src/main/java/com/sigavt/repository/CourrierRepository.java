package com.sigavt.repository;

import com.sigavt.entity.Courrier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourrierRepository extends JpaRepository<Courrier, Long> {
}
