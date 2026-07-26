content = '''package com.sigavt.repository;

import com.sigavt.entity.Colis;
import com.sigavt.enums.StatutColis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ColisRepository extends JpaRepository<Colis, Long> {
    Optional<Colis> findByNumeroTracking(String numeroTracking);
    List<Colis> findByStatut(StatutColis statut);
    List<Colis> findByAgence_Id(Long agenceId);

    @Query("SELECT c FROM Colis c WHERE c.statut = :statut AND c.dateLivraison IS NOT NULL AND c.dateLivraison < :date AND c.dateLivraison > :minDate")
    List<Colis> findByStatutAndDateLivraisonBefore(StatutColis statut, LocalDateTime date, LocalDateTime minDate);

    Page<Colis> findAll(Pageable pageable);
    Page<Colis> findByStatut(StatutColis statut, Pageable pageable);
}
'''

with open('d:/sigavt/src/main/java/com/sigavt/repository/ColisRepository.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("File written successfully")
