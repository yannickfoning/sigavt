package com.sigavt.controller;

import com.sigavt.entity.BaremeTarifColis;
import com.sigavt.repository.BaremeTarifColisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bareme-tarif-colis")
@RequiredArgsConstructor
public class BaremeTarifColisController {

    private final BaremeTarifColisRepository baremeTarifColisRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<List<BaremeTarifColis>> lister() {
        return ResponseEntity.ok(baremeTarifColisRepository.findByActifTrueOrderByPoidsMinAsc());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<BaremeTarifColis> obtenir(@PathVariable Long id) {
        return baremeTarifColisRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaremeTarifColis> creer(@RequestBody BaremeTarifColis bareme) {
        bareme.setDateCreation(java.time.LocalDateTime.now());
        bareme.setActif(true);
        return ResponseEntity.ok(baremeTarifColisRepository.save(bareme));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaremeTarifColis> mettreAJour(@PathVariable Long id, @RequestBody BaremeTarifColis bareme) {
        return baremeTarifColisRepository.findById(id)
                .map(existing -> {
                    bareme.setId(id);
                    bareme.setDateModification(java.time.LocalDateTime.now());
                    return ResponseEntity.ok(baremeTarifColisRepository.save(bareme));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        baremeTarifColisRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
