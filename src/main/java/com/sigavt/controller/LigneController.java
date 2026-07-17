package com.sigavt.controller;

import com.sigavt.dto.request.LigneRequest;
import com.sigavt.entity.Ligne;
import com.sigavt.service.LigneService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lignes")
@RequiredArgsConstructor
public class LigneController {

    private final LigneService ligneService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<Ligne> creer(@Valid @RequestBody LigneRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ligneService.creer(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<Page<Ligne>> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        size = Math.min(size, 100);
        return ResponseEntity.ok(ligneService.listerToutes(page, size, search));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<Ligne> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(ligneService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<Ligne> modifier(@PathVariable Long id, @Valid @RequestBody LigneRequest request) {
        return ResponseEntity.ok(ligneService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        ligneService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
