package com.sigavt.service.impl;

import com.sigavt.dto.request.ColisRequest;
import com.sigavt.entity.BaremeTarifColis;
import com.sigavt.entity.Colis;
import com.sigavt.entity.SupplementColis;
import com.sigavt.entity.SuiviColis;
import com.sigavt.entity.Utilisateur;
import com.sigavt.enums.ModePaiement;
import com.sigavt.enums.StatutColis;
import com.sigavt.enums.TypeColis;
import com.sigavt.enums.TypeSupplementColis;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.BaremeTarifColisRepository;
import com.sigavt.repository.ColisRepository;
import com.sigavt.repository.SupplementColisRepository;
import com.sigavt.repository.SuiviColisRepository;
import com.sigavt.repository.UtilisateurRepository;
import com.sigavt.service.ColisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ColisServiceImpl implements ColisService {

    private final ColisRepository colisRepository;
    private final SuiviColisRepository suiviColisRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final BaremeTarifColisRepository baremeTarifColisRepository;
    private final SupplementColisRepository supplementColisRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    @Transactional
    public Colis creer(ColisRequest r, String emailAgent) {
        BigDecimal tarif = calculerTarif(r.getPoidsKg(), r.isFragile(), r.isUrgent(), r.isAssure());

        Utilisateur agent = utilisateurRepository.findByEmail(emailAgent).orElse(null);

        Colis colis = Colis.builder()
                .numeroTracking(genererNumeroTracking())
                .expediteurNom(r.getExpediteurNom())
                .expediteurTel(r.getExpediteurTelephone())
                .destinataireNom(r.getDestinataireNom())
                .destinataireTel(r.getDestinataireTelephone())
                .villeDepart(r.getVilleDepart())
                .villeArrivee(r.getVilleArrivee())
                .poidsKg(r.getPoidsKg())
                .typeColis(r.getTypeColis() != null ? TypeColis.valueOf(r.getTypeColis().toUpperCase()) : TypeColis.AUTRE)
                .description(r.getDescription())
                .fragile(r.isFragile())
                .urgent(r.isUrgent())
                .assure(r.isAssure())
                .tarif(tarif)
                .modePaiement(ModePaiement.valueOf(r.getModePaiement().toUpperCase()))
                .statut(StatutColis.ENREGISTRE)
                .agence(null)
                .agent(agent)
                .dateCreation(LocalDateTime.now())
                .build();

        colis = colisRepository.save(colis);

        // Create initial tracking event
        SuiviColis suivi = SuiviColis.builder()
                .colis(colis)
                .statut(StatutColis.ENREGISTRE)
                .evenement("Colis enregistre a l'agence")
                .dateEvenement(LocalDateTime.now())
                .agent(agent)
                .build();
        suiviColisRepository.save(suivi);

        return colis;
    }

    @Override
    public Colis obtenirParId(Long id) {
        return colisRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Colis introuvable : " + id));
    }

    @Override
    public Colis obtenirParTracking(String numeroTracking) {
        return colisRepository.findByNumeroTracking(numeroTracking)
                .orElseThrow(() -> new RessourceIntrouvableException("Colis introuvable avec le tracking : " + numeroTracking));
    }

    @Override
    public Page<Colis> listerTous(int page, int size, String statut) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        if (statut != null && !statut.isBlank()) {
            return colisRepository.findByStatut(StatutColis.valueOf(statut), pageable);
        }
        return colisRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Colis mettreAJourStatut(Long id, String nouveauStatut) {
        Colis colis = obtenirParId(id);
        StatutColis statut = StatutColis.valueOf(nouveauStatut.toUpperCase());
        colis.setStatut(statut);

        if (statut == StatutColis.LIVRE) {
            colis.setDateLivraison(LocalDateTime.now());
        }

        // Add tracking event
        SuiviColis suivi = SuiviColis.builder()
                .colis(colis)
                .statut(statut)
                .evenement("Statut mis a jour : " + statut.name())
                .dateEvenement(LocalDateTime.now())
                .agent(colis.getAgent())
                .build();
        suiviColisRepository.save(suivi);

        return colisRepository.save(colis);
    }

    @Override
    @Transactional
    public void supprimer(Long id) {
        colisRepository.delete(obtenirParId(id));
    }

    private String genererNumeroTracking() {
        return "COL-" + LocalDateTime.now().format(FMT);
    }

    private BigDecimal calculerTarif(BigDecimal poidsKg, boolean fragile, boolean urgent, boolean assure) {
        // Get base tariff from database configuration
        BigDecimal tarifBase = baremeTarifColisRepository.findByPoids(poidsKg)
                .map(BaremeTarifColis::getTarifBase)
                .orElseGet(() -> {
                    // Fallback to hardcoded values if no configuration found
                    if (poidsKg.compareTo(BigDecimal.ONE) <= 0) {
                        return BigDecimal.valueOf(500);
                    } else if (poidsKg.compareTo(BigDecimal.valueOf(5)) <= 0) {
                        return BigDecimal.valueOf(1200);
                    } else if (poidsKg.compareTo(BigDecimal.valueOf(15)) <= 0) {
                        return BigDecimal.valueOf(2500);
                    } else {
                        return BigDecimal.valueOf(4000);
                    }
                });

        // Get supplements from database configuration
        BigDecimal supplement = BigDecimal.ZERO;
        if (fragile) {
            supplement = supplement.add(
                    supplementColisRepository.findByTypeSupplementAndActifTrue(TypeSupplementColis.FRAGILE)
                            .map(SupplementColis::getMontant)
                            .orElse(BigDecimal.valueOf(300))
            );
        }
        if (urgent) {
            supplement = supplement.add(
                    supplementColisRepository.findByTypeSupplementAndActifTrue(TypeSupplementColis.URGENT)
                            .map(SupplementColis::getMontant)
                            .orElse(BigDecimal.valueOf(800))
            );
        }
        if (assure) {
            supplement = supplement.add(
                    supplementColisRepository.findByTypeSupplementAndActifTrue(TypeSupplementColis.ASSURE)
                            .map(SupplementColis::getMontant)
                            .orElse(BigDecimal.valueOf(500))
            );
        }

        return tarifBase.add(supplement);
    }
}
