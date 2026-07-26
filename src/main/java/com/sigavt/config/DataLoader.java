package com.sigavt.config;

import com.sigavt.entity.Agence;
import com.sigavt.entity.Bus;
import com.sigavt.entity.Ligne;
import com.sigavt.entity.ParametresAgence;
import com.sigavt.entity.Personnel;
import com.sigavt.entity.Role;
import com.sigavt.entity.Utilisateur;
import com.sigavt.entity.Voyage;
import com.sigavt.enums.Poste;
import com.sigavt.enums.RoleNom;
import com.sigavt.enums.StatutBus;
import com.sigavt.enums.StatutEmploye;
import com.sigavt.enums.StatutLigne;
import com.sigavt.enums.TypeContrat;
import com.sigavt.repository.AgenceRepository;
import com.sigavt.repository.BusRepository;
import com.sigavt.repository.ColisRepository;
import com.sigavt.repository.CourrierRepository;
import com.sigavt.repository.LigneRepository;
import com.sigavt.repository.ParametresAgenceRepository;
import com.sigavt.repository.PersonnelRepository;
import com.sigavt.repository.RoleRepository;
import com.sigavt.repository.UtilisateurRepository;
import com.sigavt.repository.VoyageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@Profile("disabled")
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AgenceRepository agenceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ParametresAgenceRepository parametresAgenceRepository;
    private final LigneRepository ligneRepository;
    private final BusRepository busRepository;
    private final VoyageRepository voyageRepository;
    private final PersonnelRepository personnelRepository;
    private final CourrierRepository courrierRepository;
    private final ColisRepository colisRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Chargement des donnees initiales pour le profil dev...");

        // Toujours charger les données initiales pour le dev
        chargerDonneesInitiales();

        // Skip normalization for now to avoid startup issues
        // normaliserDonneesAgence();
    }

    private void chargerDonneesInitiales() {
        log.info("Insertion des donnees initiales...");

        if (roleRepository.count() == 0) {
            log.info("Creation des roles par defaut...");
            roleRepository.save(Role.builder().nom(RoleNom.ADMIN).build());
            roleRepository.save(Role.builder().nom(RoleNom.GERANT).build());
            roleRepository.save(Role.builder().nom(RoleNom.BILLETTERIE).build());
            roleRepository.save(Role.builder().nom(RoleNom.CONVOYEUR).build());
            roleRepository.save(Role.builder().nom(RoleNom.COMPTABLE).build());
            roleRepository.save(Role.builder().nom(RoleNom.RESP_FLOTTE).build());
        }

        if (agenceRepository.count() == 0) {
            Agence agence = Agence.builder()
                    .nom("Agence Voyage CM - Yaounde")
                    .ville("Yaounde")
                    .adresse("Carrefour Nlongkak, Yaounde")
                    .telephone("+237 677 000 111")
                    .email("contact@sigavt.cm")
                    .build();
            agence = agenceRepository.save(agence);

            ParametresAgence parametres = ParametresAgence.builder()
                    .nomAgence("Agence Voyage CM - Yaounde")
                    .telephone("+237 677 000 111")
                    .email("contact@sigavt.cm")
                    .villePrincipale("Yaounde")
                    .adresse("Carrefour Nlongkak, Yaounde")
                    .build();
            parametresAgenceRepository.save(parametres);

            Role adminRole = roleRepository.findByNom(RoleNom.ADMIN)
                    .orElseThrow(() -> new IllegalStateException("Role ADMIN non trouve"));

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
        } else {
            log.info("Agence deja existante, skip creation");
        }

        log.info("Donnees initiales chargees avec succes.");
    }

    private void chargerDonneesTest(Agence agence, Role adminRole) {
        log.info("Chargement des donnees de test complètes...");
        
        // Agences supplémentaires
        if (agenceRepository.count() < 3) {
            agenceRepository.save(Agence.builder()
                    .nom("Agence Douala")
                    .ville("Douala")
                    .adresse("Boulevard de la Liberté")
                    .telephone("+237 677 234 567")
                    .email("douala@sigavt.cm")
                    .build());
            agenceRepository.save(Agence.builder()
                    .nom("Agence Bafoussam")
                    .ville("Bafoussam")
                    .adresse("Quartier Administratif")
                    .telephone("+237 677 345 678")
                    .email("bafoussam@sigavt.cm")
                    .build());
        }

        // Lignes de transport
        if (ligneRepository.count() == 0) {
            ligneRepository.save(Ligne.builder()
                    .villeDepart("Yaoundé")
                    .villeArrivee("Douala")
                    .distanceKm(240)
                    .dureeMinutes(240)
                    .tarifBase(new BigDecimal("8000"))
                    .frequenceJour(8)
                    .statut(StatutLigne.ACTIVE)
                    .build());
            ligneRepository.save(Ligne.builder()
                    .villeDepart("Yaoundé")
                    .villeArrivee("Bafoussam")
                    .distanceKm(230)
                    .dureeMinutes(300)
                    .tarifBase(new BigDecimal("7500"))
                    .frequenceJour(4)
                    .statut(StatutLigne.ACTIVE)
                    .build());
            ligneRepository.save(Ligne.builder()
                    .villeDepart("Yaoundé")
                    .villeArrivee("Bamenda")
                    .distanceKm(360)
                    .dureeMinutes(420)
                    .tarifBase(new BigDecimal("10000"))
                    .frequenceJour(2)
                    .statut(StatutLigne.ACTIVE)
                    .build());
        }

        // Bus
        if (busRepository.count() == 0) {
            Ligne ligne1 = ligneRepository.findAll().get(0);
            busRepository.save(Bus.builder()
                    .immatriculation("LT-234-AB")
                    .modele("Toyota Coaster")
                    .nombrePlaces(24)
                    .ligneAssignee(ligne1)
                    .statut(StatutBus.OPERATIONNEL)
                    .prochainEntretien(LocalDate.now().plusDays(30))
                    .assuranceExpiration(LocalDate.now().plusDays(180))
                    .build());
            busRepository.save(Bus.builder()
                    .immatriculation("LT-567-CD")
                    .modele("Hyundai County")
                    .nombrePlaces(28)
                    .ligneAssignee(ligne1)
                    .statut(StatutBus.OPERATIONNEL)
                    .prochainEntretien(LocalDate.now().plusDays(45))
                    .assuranceExpiration(LocalDate.now().plusDays(200))
                    .build());
        }

        // Personnel
        if (personnelRepository.count() == 0) {
            Bus bus1 = busRepository.findAll().get(0);
            personnelRepository.save(Personnel.builder()
                    .nomComplet("Jean Pierre Mbarga")
                    .telephone("+237 699 111 222")
                    .poste(Poste.CHAUFFEUR)
                    .typeContrat(TypeContrat.CDI)
                    .salaireBase(new BigDecimal("180000"))
                    .numeroCnps("CNPS-001")
                    .numeroCni("CNI-1234567890")
                    .permisConduire("D")
                    .busAssigne(bus1)
                    .agence(agence)
                    .statut(StatutEmploye.ACTIF)
                    .dateEmbauche(LocalDate.of(2023, 1, 15))
                    .build());
            personnelRepository.save(Personnel.builder()
                    .nomComplet("Marie Claire Atangana")
                    .telephone("+237 677 555 666")
                    .poste(Poste.BILLETTERIE)
                    .typeContrat(TypeContrat.CDI)
                    .salaireBase(new BigDecimal("120000"))
                    .numeroCnps("CNPS-003")
                    .numeroCni("CNI-1122334455")
                    .agence(agence)
                    .statut(StatutEmploye.ACTIF)
                    .dateEmbauche(LocalDate.of(2023, 3, 10))
                    .build());
        }

        // Utilisateurs supplémentaires
        if (utilisateurRepository.count() < 4) {
            Role gerantRole = roleRepository.findByNom(RoleNom.GERANT)
                    .orElseThrow(() -> new IllegalStateException("Role GERANT non trouve"));
            Role billetterieRole = roleRepository.findByNom(RoleNom.BILLETTERIE)
                    .orElseThrow(() -> new IllegalStateException("Role BILLETTERIE non trouve"));
            
            utilisateurRepository.save(Utilisateur.builder()
                    .nomComplet("Gérant Yaoundé")
                    .email("gerant.yaounde@sigavt.cm")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .telephone("+237 699 111 000")
                    .role(gerantRole)
                    .agence(agence)
                    .actif(true)
                    .dateCreation(LocalDateTime.now())
                    .build());
            utilisateurRepository.save(Utilisateur.builder()
                    .nomComplet("Agent Billetterie")
                    .email("billetterie@sigavt.cm")
                    .motDePasse(passwordEncoder.encode("admin123"))
                    .telephone("+237 677 222 000")
                    .role(billetterieRole)
                    .agence(agence)
                    .actif(true)
                    .dateCreation(LocalDateTime.now())
                    .build());
        }

        log.info("Donnees de test chargees avec succes.");
    }

    private void normaliserDonneesAgence() {
        int agencesSupprimees = dedoublonnerAgences();
        int lignesSupprimees = dedoublonnerLignes();
        int busMisAJour = normaliserDatesFlotte();
        if (agencesSupprimees > 0 || lignesSupprimees > 0 || busMisAJour > 0) {
            log.info("Normalisation dev terminee: {} agence(s) dupliquee(s), {} ligne(s) dupliquee(s), {} bus avec dates actualisees.",
                    agencesSupprimees, lignesSupprimees, busMisAJour);
        }
    }

    private int dedoublonnerLignes() {
        Map<String, Ligne> lignesUniques = new LinkedHashMap<>();
        int suppressions = 0;
        List<Ligne> lignes = ligneRepository.findAll().stream()
                .sorted(Comparator.comparing(Ligne::getId))
                .toList();

        for (Ligne ligne : lignes) {
            String cle = normaliser(ligne.getVilleDepart()) + "|"
                    + normaliser(ligne.getVilleArrivee()) + "|"
                    + ligne.getDistanceKm() + "|"
                    + ligne.getDureeMinutes() + "|"
                    + montantCle(ligne.getTarifBase());
            Ligne ligneCanonique = lignesUniques.putIfAbsent(cle, ligne);
            if (ligneCanonique == null) {
                continue;
            }

            for (Bus bus : busRepository.findByLigneAssignee_Id(ligne.getId())) {
                bus.setLigneAssignee(ligneCanonique);
                busRepository.save(bus);
            }
            for (Voyage voyage : voyageRepository.findByLigne_Id(ligne.getId())) {
                voyage.setLigne(ligneCanonique);
                voyageRepository.save(voyage);
            }
            ligneRepository.delete(ligne);
            suppressions++;
        }
        return suppressions;
    }

    private int dedoublonnerAgences() {
        Map<String, Agence> agencesUniques = new LinkedHashMap<>();
        int suppressions = 0;
        List<Agence> agences = agenceRepository.findAll().stream()
                .sorted(Comparator.comparing(Agence::getId))
                .toList();

        for (Agence agence : agences) {
            String cle = normaliser(agence.getNom()) + "|"
                    + normaliser(agence.getVille()) + "|"
                    + normaliser(agence.getAdresse()) + "|"
                    + normaliser(agence.getTelephone()) + "|"
                    + normaliser(agence.getEmail());
            Agence agenceCanonique = agencesUniques.putIfAbsent(cle, agence);
            if (agenceCanonique == null) {
                continue;
            }

            utilisateurRepository.findByAgence_Id(agence.getId()).forEach(utilisateur -> {
                utilisateur.setAgence(agenceCanonique);
                utilisateurRepository.save(utilisateur);
            });
            personnelRepository.findByAgence_Id(agence.getId()).forEach(personnel -> {
                personnel.setAgence(agenceCanonique);
                personnelRepository.save(personnel);
            });
            courrierRepository.findByAgence_Id(agence.getId()).forEach(courrier -> {
                courrier.setAgence(agenceCanonique);
                courrierRepository.save(courrier);
            });
            colisRepository.findByAgence_Id(agence.getId()).forEach(colis -> {
                colis.setAgence(agenceCanonique);
                colisRepository.save(colis);
            });
            agenceRepository.delete(agence);
            suppressions++;
        }
        return suppressions;
    }

    private int normaliserDatesFlotte() {
        LocalDate aujourdHui = LocalDate.now();
        int modifications = 0;
        for (Bus bus : busRepository.findAll()) {
            boolean modifie = false;
            if (bus.getProchainEntretien() != null && bus.getProchainEntretien().isBefore(aujourdHui)) {
                bus.setProchainEntretien(aujourdHui.plusDays(90));
                modifie = true;
            }
            if (bus.getAssuranceExpiration() != null && bus.getAssuranceExpiration().isBefore(aujourdHui)) {
                bus.setAssuranceExpiration(aujourdHui.plusYears(1));
                modifie = true;
            }
            if (modifie) {
                busRepository.save(bus);
                modifications++;
            }
        }
        return modifications;
    }

    private String normaliser(String valeur) {
        return valeur == null ? "" : valeur.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    private String montantCle(BigDecimal montant) {
        return montant == null ? "" : montant.stripTrailingZeros().toPlainString();
    }
}
