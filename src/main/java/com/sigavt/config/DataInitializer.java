package com.sigavt.config;

import com.sigavt.entity.*;
import com.sigavt.enums.*;
import com.sigavt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.*;

@Component
@Profile({"h2", "dev", "test"}) // Uniquement développement/test, JAMAIS production
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    @Autowired
    private AgenceRepository agenceRepository;
    
    @Autowired
    private LigneRepository ligneRepository;
    
    @Autowired
    private BusRepository busRepository;
    
    @Autowired
    private PersonnelRepository personnelRepository;
    
    @Autowired
    private VoyageRepository voyageRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.data.seed-on-startup:false}")
    private boolean seedOnStartup;

    @Override
    public void run(String... args) throws Exception {
        if (!seedOnStartup) {
            return;
        }

        System.out.println("Seeding database with initial data...");
        System.out.println("Cleaning existing data first...");
        
        // Delete in correct order to respect foreign key constraints
        voyageRepository.deleteAll();
        busRepository.deleteAll();
        ligneRepository.deleteAll();
        personnelRepository.deleteAll();
        utilisateurRepository.deleteAll();
        roleRepository.deleteAll();
        agenceRepository.deleteAll();

        // Créer les agences
        Agence agenceYaounde = createAgence("Yaoundé", "Avenue Charles de Gaulle", "237 600 000 001");
        Agence agenceDouala = createAgence("Douala", "Boulevard de la Liberté", "237 600 000 002");
        Agence agenceBafoussam = createAgence("Bafoussam", "Place des Fêtes", "237 600 000 003");
        
        agenceRepository.saveAll(Arrays.asList(agenceYaounde, agenceDouala, agenceBafoussam));

        // Créer le rôle admin
        Role adminRole = Role.builder().nom(RoleNom.ADMIN).build();
        roleRepository.save(adminRole);

        // Créer l'admin
        Utilisateur admin = createAdmin(adminRole, agenceYaounde);
        utilisateurRepository.save(admin);

        // Créer les lignes
        Ligne ligne1 = createLigne("Yaoundé", "Douala", 250, new BigDecimal("5000"), 240);
        Ligne ligne2 = createLigne("Yaoundé", "Bafoussam", 350, new BigDecimal("7000"), 300);
        Ligne ligne3 = createLigne("Douala", "Bafoussam", 300, new BigDecimal("6000"), 270);
        
        ligneRepository.saveAll(Arrays.asList(ligne1, ligne2, ligne3));

        // Créer les bus
        Bus bus1 = createBus("CE-123-AB", "Mercedes-Benz Sprinter", 18, agenceYaounde);
        Bus bus2 = createBus("CE-456-CD", "Mercedes-Benz Sprinter", 18, agenceDouala);
        Bus bus3 = createBus("CE-789-EF", "Toyota Coaster", 25, agenceYaounde);
        
        busRepository.saveAll(Arrays.asList(bus1, bus2, bus3));

        // Créer les employés
        Personnel emp1 = createEmploye("Martin Jean", Poste.CHAUFFEUR, TypeContrat.CDI, new BigDecimal("150000"), agenceYaounde);
        Personnel emp2 = createEmploye("Mbarga Marie", Poste.BILLETTERIE, TypeContrat.CDD, new BigDecimal("120000"), agenceDouala);
        Personnel emp3 = createEmploye("Nkodo Paul", Poste.CONVOYEUR, TypeContrat.CDI, new BigDecimal("130000"), agenceYaounde);
        
        personnelRepository.saveAll(Arrays.asList(emp1, emp2, emp3));

        // Créer des voyages pour aujourd'hui
        LocalDate today = LocalDate.now();
        Voyage voyage1 = createVoyage(ligne1, bus1, today, LocalTime.of(8, 0));
        Voyage voyage2 = createVoyage(ligne2, bus3, today, LocalTime.of(9, 0));
        Voyage voyage3 = createVoyage(ligne1, bus2, today, LocalTime.of(14, 0));
        
        voyageRepository.saveAll(Arrays.asList(voyage1, voyage2, voyage3));

        System.out.println("Database seeded successfully!");
    }

    private Agence createAgence(String nom, String adresse, String telephone) {
        Agence agence = Agence.builder()
                .nom(nom)
                .ville(nom)
                .adresse(adresse)
                .telephone(telephone)
                .email(nom.toLowerCase().replace(" ", "") + "@sigavt.cm")
                .build();
        return agence;
    }

    private Utilisateur createAdmin(Role role, Agence agence) {
        Utilisateur admin = Utilisateur.builder()
                .nomComplet("Admin SIGAVT")
                .email("admin@sigavt.cm")
                .motDePasse(passwordEncoder.encode("admin123"))
                .role(role)
                .agence(agence)
                .build();
        return admin;
    }

    private Ligne createLigne(String villeDepart, String villeArrivee, int distanceKm, BigDecimal tarifBase, int dureeMinutes) {
        Ligne ligne = Ligne.builder()
                .villeDepart(villeDepart)
                .villeArrivee(villeArrivee)
                .distanceKm(distanceKm)
                .tarifBase(tarifBase)
                .dureeMinutes(dureeMinutes)
                .statut(StatutLigne.ACTIVE)
                .build();
        return ligne;
    }

    private Bus createBus(String immatriculation, String modele, int nombrePlaces, Agence agence) {
        Bus bus = Bus.builder()
                .immatriculation(immatriculation)
                .modele(modele)
                .nombrePlaces(nombrePlaces)
                .statut(StatutBus.OPERATIONNEL)
                .prochainEntretien(LocalDate.now().plusMonths(6))
                .assuranceExpiration(LocalDate.now().plusMonths(12))
                .build();
        bus.setClasseBus(ClasseSiege.CLASSIQUE);
        return bus;
    }

    private Personnel createEmploye(String nomComplet, Poste poste, TypeContrat typeContrat, BigDecimal salaireBase, Agence agence) {
        Personnel personnel = Personnel.builder()
                .nomComplet(nomComplet)
                .poste(poste)
                .typeContrat(typeContrat)
                .salaireBase(salaireBase)
                .agence(agence)
                .statut(StatutEmploye.ACTIF)
                .dateEmbauche(LocalDate.now().minusMonths(6))
                .telephone("237 600 000 00" + (int)(Math.random() * 9 + 1))
                .build();
        return personnel;
    }

    private Voyage createVoyage(Ligne ligne, Bus bus, LocalDate dateVoyage, LocalTime heureDepart) {
        Voyage voyage = Voyage.builder()
                .ligne(ligne)
                .bus(bus)
                .dateVoyage(dateVoyage)
                .heureDepart(heureDepart)
                .statut(StatutVoyage.PLANIFIE)
                .placesDisponibles(bus.getNombrePlaces())
                .build();
        return voyage;
    }
}
