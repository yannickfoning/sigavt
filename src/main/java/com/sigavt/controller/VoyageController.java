package com.sigavt.controller;

import com.sigavt.dto.request.VoyageRequest;
import com.sigavt.dto.response.VoyageResponse;
import com.sigavt.entity.Siege;
import com.sigavt.entity.Voyage;
import com.sigavt.repository.SiegeRepository;
import com.sigavt.service.VoyageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public ResponseEntity<?> lister(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (date != null) {
            List<VoyageResponse> responses = voyageService.listerParDate(date).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        }
        size = Math.min(size, 100);
        return ResponseEntity.ok(voyageService.listerTous(page, size).map(this::toResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<VoyageResponse> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(voyageService.obtenirParId(id)));
    }

    @GetMapping("/{id}/sieges")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','RESP_FLOTTE')")
    public ResponseEntity<List<Siege>> obtenirSieges(@PathVariable Long id) {
        return ResponseEntity.ok(siegeRepository.findByVoyage_Id(id));
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

    private VoyageResponse toResponse(Voyage voyage) {
        return VoyageResponse.builder()
                .id(voyage.getId())
                .villeDepart(voyage.getLigne() != null ? voyage.getLigne().getVilleDepart() : null)
                .villeArrivee(voyage.getLigne() != null ? voyage.getLigne().getVilleArrivee() : null)
                .dateVoyage(voyage.getDateVoyage())
                .heureDepart(voyage.getHeureDepart())
                .placesDisponibles(voyage.getPlacesDisponibles())
                .tarifBase(voyage.getLigne() != null ? voyage.getLigne().getTarifBase() : null)
                .statut(voyage.getStatut())
                .busImmatriculation(voyage.getBus() != null ? voyage.getBus().getImmatriculation() : null)
                .chauffeurNom(voyage.getChauffeur() != null ? voyage.getChauffeur().getNomComplet() : null)
                .build();
    }
}
