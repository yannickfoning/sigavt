package com.sigavt.controller;

import com.sigavt.entity.Agence;
import com.sigavt.service.AgenceService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agences")
@RequiredArgsConstructor
public class AgenceController {

    private final AgenceService agenceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<Agence> creer(@Valid @RequestBody Agence agence) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenceService.creer(agence));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<Page<Agence>> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        size = Math.min(size, 100);
        return ResponseEntity.ok(agenceService.listerToutes(page, size, search));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<Agence> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(agenceService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<Agence> modifier(@PathVariable Long id, @Valid @RequestBody Agence agence) {
        return ResponseEntity.ok(agenceService.modifier(id, agence));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        agenceService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
