package com.sigavt.controller;

import com.sigavt.dto.request.BusRequest;
import com.sigavt.entity.Bus;
import com.sigavt.service.BusService;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','RESP_FLOTTE')")
    public ResponseEntity<Bus> creer(@Valid @RequestBody BusRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(busService.creer(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<?> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String statut) {
        size = Math.min(size, 100);
        return ResponseEntity.ok(busService.listerTous(page, size, statut));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','CHAUFFEUR','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<Bus> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(busService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','RESP_FLOTTE')")
    public ResponseEntity<Bus> modifier(@PathVariable Long id, @Valid @RequestBody BusRequest request) {
        return ResponseEntity.ok(busService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESP_FLOTTE')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        busService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
