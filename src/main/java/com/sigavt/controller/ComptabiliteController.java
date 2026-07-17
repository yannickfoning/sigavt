package com.sigavt.controller;

import com.sigavt.dto.request.EcritureComptableRequest;
import com.sigavt.entity.EcritureComptable;
import com.sigavt.service.ComptabiliteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comptabilite")
@RequiredArgsConstructor
public class ComptabiliteController {

    private final ComptabiliteService comptabiliteService;

    @PostMapping("/ecritures")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<EcritureComptable> creer(@Valid @RequestBody EcritureComptableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comptabiliteService.creer(request));
    }

    @GetMapping("/ecritures")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<List<EcritureComptable>> lister(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        if (debut != null && fin != null) {
            return ResponseEntity.ok(comptabiliteService.listerParPeriode(debut, fin));
        }
        // Default to last 30 days if no dates provided
        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusDays(30);
        return ResponseEntity.ok(comptabiliteService.listerParPeriode(monthAgo, today));
    }

    @GetMapping("/ecritures/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<EcritureComptable> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(comptabiliteService.obtenirParId(id));
    }

    @GetMapping("/bilan")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<Map<String, Object>> genererBilan(
            @RequestParam(defaultValue = "0") int mois,
            @RequestParam(defaultValue = "0") int annee) {
        return ResponseEntity.ok(comptabiliteService.genererBilan(mois, annee));
    }

    @DeleteMapping("/ecritures/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        comptabiliteService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
