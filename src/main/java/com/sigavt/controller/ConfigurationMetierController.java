package com.sigavt.controller;

import com.sigavt.entity.ConfigurationMetier;
import com.sigavt.enums.CategorieConfiguration;
import com.sigavt.repository.ConfigurationMetierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configuration")
@RequiredArgsConstructor
public class ConfigurationMetierController {

    private final ConfigurationMetierRepository configurationMetierRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<List<ConfigurationMetier>> lister(
            @RequestParam(required = false) CategorieConfiguration categorie) {
        if (categorie != null) {
            return ResponseEntity.ok(configurationMetierRepository.findByCategorie(categorie));
        }
        return ResponseEntity.ok(configurationMetierRepository.findAll());
    }

    @GetMapping("/{cle}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<ConfigurationMetier> obtenir(@PathVariable String cle) {
        return configurationMetierRepository.findByCle(cle)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfigurationMetier> creer(@RequestBody ConfigurationMetier configuration) {
        configuration.setDateModification(java.time.LocalDateTime.now());
        return ResponseEntity.ok(configurationMetierRepository.save(configuration));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfigurationMetier> mettreAJour(@PathVariable Long id, @RequestBody ConfigurationMetier configuration) {
        return configurationMetierRepository.findById(id)
                .map(existing -> {
                    configuration.setId(id);
                    configuration.setDateModification(java.time.LocalDateTime.now());
                    return ResponseEntity.ok(configurationMetierRepository.save(configuration));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        configurationMetierRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
