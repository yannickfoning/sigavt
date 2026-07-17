package com.sigavt.service.impl;

import com.sigavt.dto.request.BilletRequest;
import com.sigavt.entity.*;
import com.sigavt.enums.ModePaiement;
import com.sigavt.enums.StatutBillet;
import com.sigavt.enums.StatutSiege;
import com.sigavt.enums.TypeTarif;
import com.sigavt.exception.RegleMetierException;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.*;
import com.sigavt.service.BilletService;
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
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BilletServiceImpl implements BilletService {

    private final BilletRepository billetRepository;
    private final VoyageRepository voyageRepository;
    private final SiegeRepository siegeRepository;
    private final UtilisateurRepository utilisateurRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Override
    @Transactional
    public Billet vendre(BilletRequest r, String emailAgent) {
        // Use pessimistic lock to prevent race condition on seat reservation
        Voyage voyage = voyageRepository.findByIdWithLock(r.getVoyageId())
                .orElseThrow(() -> new RessourceIntrouvableException("Voyage introuvable : " + r.getVoyageId()));

        if (voyage.getPlacesDisponibles() == null || voyage.getPlacesDisponibles() <= 0) {
            throw new RegleMetierException("Aucune place disponible pour ce voyage");
        }

        // Resolution du siege : par id, par numero, ou premier libre
        Siege siege = resoudreSiege(voyage, r);

        if (siege != null && siege.getStatut() == StatutSiege.OCCUPE) {
            throw new RegleMetierException("Le siege " + siege.getNumero() + " est deja occupe");
        }
        if (siege != null) {
            siege.setStatut(StatutSiege.OCCUPE);
            siegeRepository.save(siege);
        }

        TypeTarif typeTarif = TypeTarif.valueOf(r.getTypeTarif());
        BigDecimal tarifBase = voyage.getLigne().getTarifBase();
        BigDecimal prix = calculerPrix(tarifBase, typeTarif);

        Utilisateur agent = utilisateurRepository.findByEmail(emailAgent).orElse(null);

        Billet billet = Billet.builder()
                .numeroBillet("BIL-" + LocalDateTime.now().format(FMT))
                .voyage(voyage)
                .siege(siege)
                .passagerNom(r.getPassagerNom())
                .passagerTelephone(r.getPassagerTelephone())
                .typeTarif(typeTarif)
                .prix(prix)
                .modePaiement(ModePaiement.valueOf(r.getModePaiement()))
                .statut(StatutBillet.VALIDE)
                .agentVente(agent)
                .build();

        billet = billetRepository.save(billet);

        voyage.setPlacesDisponibles(voyage.getPlacesDisponibles() - 1);
        voyageRepository.save(voyage);

        return billet;
    }

    private Siege resoudreSiege(Voyage voyage, BilletRequest r) {
        if (r.getSiegeId() != null) {
            return siegeRepository.findById(r.getSiegeId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Siege introuvable : " + r.getSiegeId()));
        }
        List<Siege> sieges = siegeRepository.findByVoyage_Id(voyage.getId());
        if (r.getNumeroSiege() != null) {
            return sieges.stream()
                    .filter(s -> s.getNumero().equalsIgnoreCase(r.getNumeroSiege()))
                    .findFirst()
                    .orElseThrow(() -> new RessourceIntrouvableException("Siege " + r.getNumeroSiege() + " introuvable"));
        }
        Optional<Siege> premierLibre = sieges.stream()
                .filter(s -> s.getStatut() == StatutSiege.LIBRE)
                .findFirst();
        return premierLibre.orElse(null);
    }

    private BigDecimal calculerPrix(BigDecimal tarifBase, TypeTarif typeTarif) {
        return switch (typeTarif) {
            case DEMI_TARIF_ENFANT -> tarifBase.multiply(BigDecimal.valueOf(0.5));
            case TARIF_GROUPE -> tarifBase.multiply(BigDecimal.valueOf(0.85));
            default -> tarifBase;
        };
    }

    @Override
    public List<Billet> listerParVoyage(Long voyageId) {
        return billetRepository.findByVoyage_Id(voyageId);
    }

    @Override
    public List<Billet> listerParDate(LocalDate date) {
        return billetRepository.findByDateEmissionBetween(
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        );
    }

    @Override
    public Page<Billet> listerTous(int page, int size, String search, String statut) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateEmission").descending());
        if (search != null && !search.isBlank()) {
            return billetRepository.findByPassagerNomContainingIgnoreCase(search.trim(), pageable);
        }
        if (statut != null && !statut.isBlank()) {
            return billetRepository.findByStatut(StatutBillet.valueOf(statut), pageable);
        }
        return billetRepository.findAll(pageable);
    }

    @Override
    public Billet obtenirParId(Long id) {
        return billetRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Billet introuvable : " + id));
    }

    @Override
    @Transactional
    public Billet annuler(Long id) {
        Billet billet = obtenirParId(id);
        if (billet.getStatut() == StatutBillet.ANNULE) {
            throw new RegleMetierException("Ce billet est deja annule");
        }
        billet.setStatut(StatutBillet.ANNULE);

        if (billet.getSiege() != null) {
            billet.getSiege().setStatut(StatutSiege.LIBRE);
            siegeRepository.save(billet.getSiege());
        }
        Voyage voyage = billet.getVoyage();
        voyage.setPlacesDisponibles(voyage.getPlacesDisponibles() + 1);
        voyageRepository.save(voyage);

        return billetRepository.save(billet);
    }
}
}
