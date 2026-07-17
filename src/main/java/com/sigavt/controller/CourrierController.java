package com.sigavt.controller;

import com.sigavt.dto.request.CourrierRequest;
import com.sigavt.entity.Courrier;
import com.sigavt.service.CourrierService;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courriers")
@RequiredArgsConstructor
public class CourrierController {

    private final CourrierService courrierService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<Courrier> creer(@Valid @RequestBody CourrierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courrierService.creer(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','COMPTABLE')")
    public ResponseEntity<List<Courrier>> lister() {
        return ResponseEntity.ok(courrierService.listerTous());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','COMPTABLE')")
    public ResponseEntity<Courrier> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(courrierService.obtenirParId(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<Courrier> modifier(@PathVariable Long id, @Valid @RequestBody CourrierRequest request) {
        return ResponseEntity.ok(courrierService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        courrierService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
