package com.sigavt.config;

import com.sigavt.entity.*;
import com.sigavt.enums.*;
import com.sigavt.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AgenceRepository agenceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ParametresAgenceRepository parametresAgenceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Chargement des données initiales pour le profil dev...");
        
        // Charger les données initiales uniquement si la base est vide
        if (utilisateurRepository.count() == 0) {
            chargerDonneesInitiales();
        } else {
            log.info("La base de données contient déjà des données, skipping initial data loading.");
        }
    }

    private void chargerDonneesInitiales() {
        log.info("Insertion des données initiales...");
        
        // Créer les rôles s'ils n'existent pas
        if (roleRepository.count() == 0) {
            log.info("Création des rôles par défaut...");
            roleRepository.save(Role.builder().nom(RoleNom.ADMIN).build());
            roleRepository.save(Role.builder().nom(RoleNom.GERANT).build());
            roleRepository.save(Role.builder().nom(RoleNom.BILLETTERIE).build());
            roleRepository.save(Role.builder().nom(RoleNom.CONVOYEUR).build());
            roleRepository.save(Role.builder().nom(RoleNom.COMPTABLE).build());
            roleRepository.save(Role.builder().nom(RoleNom.RESP_FLOTTE).build());
        }
        
        // Agence
        Agence agence = Agence.builder()
                .nom("Agence Voyage CM — Yaoundé")
                .ville("Yaoundé")
                .adresse("Carrefour Nlongkak, Yaoundé")
                .telephone("+237 677 000 111")
                .email("contact@sigavt.cm")
                .build();
        agence = agenceRepository.save(agence);

        // Paramètres agence
        ParametresAgence parametres = ParametresAgence.builder()
                .nomAgence("Agence Voyage CM — Yaoundé")
                .telephone("+237 677 000 111")
                .email("contact@sigavt.cm")
                .villePrincipale("Yaoundé")
                .adresse("Carrefour Nlongkak, Yaoundé")
                .build();
        parametresAgenceRepository.save(parametres);

        // Compte admin par défaut
        Role adminRole = roleRepository.findByNom(RoleNom.ADMIN)
                .orElseThrow(() -> new RuntimeException("Role ADMIN non trouvé"));
        
        Utilisateur admin = Utilisateur.builder()
                .nomComplet("Administrateur SIGAVT")
                .email("admin@sigavt.cm")
                .motDePasse(passwordEncoder.encode("admin123"))
                .telephone("+237 677 000 111")
                .role(adminRole)
                .agence(agence)
                .actif(true)
                .dateCreation(LocalDateTime.now())
                .build();
        utilisateurRepository.save(admin);

        log.info("Données initiales chargées avec succès.");
    }
}
