package com.sigavt.controller;

import com.sigavt.entity.Utilisateur;
import com.sigavt.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERANT')")
    public ResponseEntity<Page<Utilisateur>> getAllUtilisateurs(Pageable pageable) {
        return ResponseEntity.ok(utilisateurRepository.findAll(pageable));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERANT')")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateursWithoutPagination() {
        return ResponseEntity.ok(utilisateurRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERANT')")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        return utilisateurRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/agence/{agenceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERANT')")
    public ResponseEntity<List<Utilisateur>> getUtilisateursByAgence(@PathVariable Long agenceId) {
        return ResponseEntity.ok(utilisateurRepository.findByAgence_Id(agenceId));
    }
}
