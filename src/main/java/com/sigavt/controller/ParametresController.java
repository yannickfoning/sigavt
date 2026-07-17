package com.sigavt.controller;

import com.sigavt.dto.request.ParametresAgenceRequest;
import com.sigavt.entity.ParametresAgence;
import com.sigavt.service.ParametresService;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parametres")
@RequiredArgsConstructor
public class ParametresController {

    private final ParametresService parametresService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<ParametresAgence> obtenir() {
        return ResponseEntity.ok(parametresService.obtenir());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERANT')")
    public ResponseEntity<ParametresAgence> mettreAJour(@Valid @RequestBody ParametresAgenceRequest request) {
        return ResponseEntity.ok(parametresService.mettreAJour(request));
    }
}
