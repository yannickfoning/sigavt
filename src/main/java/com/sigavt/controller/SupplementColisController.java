package com.sigavt.controller;

import com.sigavt.entity.SupplementColis;
import com.sigavt.enums.TypeSupplementColis;
import com.sigavt.repository.SupplementColisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplement-colis")
@RequiredArgsConstructor
public class SupplementColisController {

    private final SupplementColisRepository supplementColisRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<List<SupplementColis>> lister() {
        return ResponseEntity.ok(supplementColisRepository.findByActifTrue());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<SupplementColis> obtenir(@PathVariable Long id) {
        return supplementColisRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<SupplementColis> obtenirParType(@PathVariable TypeSupplementColis type) {
        return supplementColisRepository.findByTypeSupplementAndActifTrue(type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupplementColis> creer(@RequestBody SupplementColis supplement) {
        supplement.setDateCreation(java.time.LocalDateTime.now());
        supplement.setActif(true);
        return ResponseEntity.ok(supplementColisRepository.save(supplement));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SupplementColis> mettreAJour(@PathVariable Long id, @RequestBody SupplementColis supplement) {
        return supplementColisRepository.findById(id)
                .map(existing -> {
                    supplement.setId(id);
                    return ResponseEntity.ok(supplementColisRepository.save(supplement));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        supplementColisRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
