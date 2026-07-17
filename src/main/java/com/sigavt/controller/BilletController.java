package com.sigavt.controller;

import com.sigavt.dto.request.BilletRequest;
import com.sigavt.entity.Billet;
import com.sigavt.service.BilletService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/billets")
@RequiredArgsConstructor
public class BilletController {

    private final BilletService billetService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE')")
    public ResponseEntity<Billet> vendre(@Valid @RequestBody BilletRequest request, Authentication authentication) {
        String email = authentication != null ? authentication.getName() : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(billetService.vendre(request, email));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','COMPTABLE')")
    public ResponseEntity<?> lister(
            @RequestParam(required = false) Long voyageId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String statut) {
        if (voyageId != null) {
            return ResponseEntity.ok(billetService.listerParVoyage(voyageId));
        }
        if (date != null) {
            return ResponseEntity.ok(billetService.listerParDate(date));
        }
        size = Math.min(size, 100);
        return ResponseEntity.ok(billetService.listerTous(page, size, search, statut));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE','CONVOYEUR','COMPTABLE')")
    public ResponseEntity<Billet> obtenir(@PathVariable Long id) {
        return ResponseEntity.ok(billetService.obtenirParId(id));
    }

    @PostMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('ADMIN','GERANT','BILLETTERIE')")
    public ResponseEntity<Billet> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(billetService.annuler(id));
    }
}
