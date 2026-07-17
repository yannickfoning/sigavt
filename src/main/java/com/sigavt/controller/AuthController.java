package com.sigavt.controller;

import com.sigavt.dto.request.LoginRequest;
import com.sigavt.dto.request.UtilisateurRequest;
import com.sigavt.dto.response.LoginResponse;
import com.sigavt.entity.Utilisateur;
import com.sigavt.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/inscription")
    public ResponseEntity<Utilisateur> inscrire(@Valid @RequestBody UtilisateurRequest request) {
        // Le service ignore désormais le rôle envoyé par le client sur cette route publique
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.inscrirePublic(request));
    }

    // Nouvelle route, réservée aux admins, pour créer des comptes avec un rôle choisi
    @PostMapping("/utilisateurs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Utilisateur> creerUtilisateur(@Valid @RequestBody UtilisateurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.creerAvecRole(request));
    }
}
