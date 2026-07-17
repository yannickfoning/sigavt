package com.sigavt.service.impl;

import com.sigavt.dto.request.LoginRequest;
import com.sigavt.dto.request.UtilisateurRequest;
import com.sigavt.dto.response.LoginResponse;
import com.sigavt.entity.Agence;
import com.sigavt.entity.Role;
import com.sigavt.entity.Utilisateur;
import com.sigavt.enums.RoleNom;
import com.sigavt.exception.RegleMetierException;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.AgenceRepository;
import com.sigavt.repository.RoleRepository;
import com.sigavt.repository.UtilisateurRepository;
import com.sigavt.security.JwtUtil;
import com.sigavt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final AgenceRepository agenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
            );
            String role = auth.getAuthorities().stream()
                    .findFirst().map(GrantedAuthority::getAuthority).orElse("ROLE_BILLETTERIE")
                    .replace("ROLE_", "");

            Utilisateur u = utilisateurRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable"));

            String token = jwtUtil.genererToken(u.getEmail(), role);

            return LoginResponse.builder()
                    .token(token)
                    .nomComplet(u.getNomComplet())
                    .email(u.getEmail())
                    .role(role)
                    .build();
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }
    }

    @Override
    public Utilisateur inscrirePublic(UtilisateurRequest request) {
        // Rôle forcé côté serveur, jamais lu depuis le client sur cette route publique
        request.setRole(RoleNom.BILLETTERIE.name());
        return creerAvecRole(request);
    }

    @Override
    public Utilisateur creerAvecRole(UtilisateurRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RegleMetierException("Un utilisateur avec cet email existe deja");
        }
        Role role = roleRepository.findByNom(RoleNom.valueOf(request.getRole()))
                .orElseThrow(() -> new RessourceIntrouvableException("Role introuvable : " + request.getRole()));

        Agence agence = null;
        if (request.getAgenceId() != null) {
            agence = agenceRepository.findById(request.getAgenceId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Agence introuvable"));
        }

        Utilisateur u = Utilisateur.builder()
                .nomComplet(request.getNomComplet())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .telephone(request.getTelephone())
                .role(role)
                .agence(agence)
                .actif(true)
                .build();

        return utilisateurRepository.save(u);
    }

    @Override
    public Utilisateur inscrire(UtilisateurRequest request) {
        // Méthode dépréciée, utilise inscrirePublic ou creerAvecRole
        return creerAvecRole(request);
    }
}
