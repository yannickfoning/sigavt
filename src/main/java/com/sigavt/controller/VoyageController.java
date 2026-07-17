package com.sigavt.controller;

import com.sigavt.dto.request.VoyageRequest;
import com.sigavt.entity.Siege;
import com.sigavt.entity.Voyage;
import com.sigavt.repository.SiegeRepository;
import com.sigavt.service.VoyageService;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/voyages")
@RequiredArgsConstructor
public class VoyageController {

    private final VoyageService voyageService;
    private final SiegeRepository siegeRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','RESP_FLOTTE')")
    public ResponseEntity<Voyage> creer(@Valid @RequestBody VoyageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(voyageService.creer(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<?> lister(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (date != null) {
            return ResponseEntity.ok(voyageService.listerParDate(date));
        }
        size = Math.min(size, 100);
        return ResponseEntity.ok(voyageService.listerTous(page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<Voyage> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(voyageService.obtenirParId(id));
    }

    @GetMapping("/{id}/sieges")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','RESP_FLOTTE')")
    public ResponseEntity<List<Siege>> obtenirSieges(@PathVariable Long id) {
        return ResponseEntity.ok(siegeRepository.findByVoyageId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','RESP_FLOTTE')")
    public ResponseEntity<Voyage> modifier(@PathVariable Long id, @Valid @RequestBody VoyageRequest request) {
        return ResponseEntity.ok(voyageService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESP_FLOTTE')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        voyageService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
