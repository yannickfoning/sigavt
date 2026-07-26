package com.sigavt.repository;

import com.sigavt.entity.SupplementColis;
import com.sigavt.enums.TypeSupplementColis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplementColisRepository extends JpaRepository<SupplementColis, Long> {

    List<SupplementColis> findByActifTrue();

    Optional<SupplementColis> findByTypeSupplementAndActifTrue(TypeSupplementColis typeSupplement);
}
