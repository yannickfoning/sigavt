package com.sigavt.controller;

import com.sigavt.dto.request.BulletinPaieRequest;
import com.sigavt.entity.BulletinPaie;
import com.sigavt.service.PaieService;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paie")
@RequiredArgsConstructor
public class PaieController {

    private final PaieService paieService;

    @PostMapping("/bulletins")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<BulletinPaie> generer(@Valid @RequestBody BulletinPaieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paieService.generer(request));
    }

    @GetMapping("/bulletins")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<List<BulletinPaie>> lister(
            @RequestParam(required = false) String periode) {
        if (periode != null) {
            return ResponseEntity.ok(paieService.listerParPeriode(periode));
        }
        return ResponseEntity.ok(paieService.listerParPeriode(""));
    }

    @GetMapping("/bulletins/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<BulletinPaie> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(paieService.obtenirParId(id));
    }

    @PostMapping("/bulletins/{id}/payer")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<BulletinPaie> marquerPaye(@PathVariable Long id) {
        return ResponseEntity.ok(paieService.marquerPaye(id));
    }

    @DeleteMapping("/bulletins/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        paieService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<List<BulletinPaie>> stats(
            @RequestParam(required = false) Integer mois,
            @RequestParam(required = false) Integer annee) {
        if (mois != null && annee != null) {
            String periode = String.format("%02d-%d", mois, annee);
            return ResponseEntity.ok(paieService.listerParPeriode(periode));
        }
        return ResponseEntity.ok(paieService.listerParPeriode(""));
    }
}
