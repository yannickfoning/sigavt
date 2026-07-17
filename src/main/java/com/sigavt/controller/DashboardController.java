package com.sigavt.controller;

import com.sigavt.dto.response.DashboardResponse;
import com.sigavt.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<DashboardResponse> obtenir() {
        return ResponseEntity.ok(dashboardService.obtenirTableauDeBord());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<DashboardResponse> getStats() {
        return ResponseEntity.ok(dashboardService.obtenirTableauDeBord());
    }

    @GetMapping("/departs")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR')")
    public ResponseEntity<?> getDeparts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate dateQuery = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(dashboardService.getDeparts(dateQuery));
    }

    @GetMapping("/recettes-semaine")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<?> getRecettesSemaine() {
        return ResponseEntity.ok(dashboardService.getRecettesSemaine());
    }

    @GetMapping("/top-lignes")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE')")
    public ResponseEntity<?> getTopLignes() {
        return ResponseEntity.ok(dashboardService.getTopLignes());
    }

    @GetMapping("/alertes")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','RESP_FLOTTE')")
    public ResponseEntity<?> getAlertes() {
        return ResponseEntity.ok(dashboardService.getAlertes());
    }
}
