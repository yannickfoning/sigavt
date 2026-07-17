package com.sigavt.controller;

import com.sigavt.dto.request.PersonnelRequest;
import com.sigavt.entity.Personnel;
import com.sigavt.service.PersonnelService;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personnel")
@RequiredArgsConstructor
public class PersonnelController {

    private final PersonnelService personnelService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<Personnel> creer(@Valid @RequestBody PersonnelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personnelService.creer(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE','RESP_FLOTTE','BILLETTERIE')")
    public ResponseEntity<?> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String poste,
            @RequestParam(required = false) String recherche) {
        size = Math.min(size, 100);
        return ResponseEntity.ok(personnelService.listerTous(page, size, poste, recherche));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','COMPTABLE','RESP_FLOTTE')")
    public ResponseEntity<Personnel> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(personnelService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<Personnel> modifier(@PathVariable Long id, @Valid @RequestBody PersonnelRequest request) {
        return ResponseEntity.ok(personnelService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        personnelService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
