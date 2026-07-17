package com.sigavt.controller;

import com.sigavt.dto.request.ColisRequest;
import com.sigavt.entity.Colis;
import com.sigavt.service.ColisService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/colis")
@RequiredArgsConstructor
public class ColisController {

    private final ColisService colisService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE')")
    public ResponseEntity<Colis> creer(@Valid @RequestBody ColisRequest request, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(colisService.creer(request, email));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR')")
    public ResponseEntity<?> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String statut) {
        size = Math.min(size, 100);
        return ResponseEntity.ok(colisService.listerTous(page, size, statut));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR')")
    public ResponseEntity<Colis> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(colisService.obtenirParId(id));
    }

    @GetMapping("/tracking/{numero}")
    public ResponseEntity<Colis> obtenirParTracking(@PathVariable String numero) {
        return ResponseEntity.ok(colisService.obtenirParTracking(numero));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','CONVOYEUR')")
    public ResponseEntity<Colis> mettreAJourStatut(@PathVariable Long id, @RequestParam String statut) {
        return ResponseEntity.ok(colisService.mettreAJourStatut(id, statut));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        colisService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
