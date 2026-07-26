package com.sigavt.service.impl;

import com.sigavt.dto.request.BusRequest;
import com.sigavt.entity.Bus;
import com.sigavt.entity.Ligne;
import com.sigavt.entity.Voyage;
import com.sigavt.enums.StatutBus;
import com.sigavt.enums.StatutVoyage;
import com.sigavt.exception.RegleMetierException;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.BusRepository;
import com.sigavt.repository.LigneRepository;
import com.sigavt.repository.VoyageRepository;
import com.sigavt.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final LigneRepository ligneRepository;
    private final VoyageRepository voyageRepository;

    @Override
    public Bus creer(BusRequest r) {
        if (r.getImmatriculation() == null || r.getImmatriculation().isBlank()) {
            throw new RegleMetierException("L'immatriculation est obligatoire");
        }
        if (r.getNombrePlaces() == null || r.getNombrePlaces() <= 0) {
            throw new RegleMetierException("Le nombre de places est obligatoire et doit être positif");
        }
        if (busRepository.findByImmatriculation(r.getImmatriculation()).isPresent()) {
            throw new RegleMetierException("Un bus avec cette immatriculation existe deja");
        }
        Bus bus = Bus.builder()
                .immatriculation(r.getImmatriculation())
                .modele(r.getModele())
                .nombrePlaces(r.getNombrePlaces())
                .ligneAssignee(resoudreLigne(r.getLigneAssigneeId()))
                .prochainEntretien(r.getProchainEntretien())
                .assuranceExpiration(r.getAssuranceExpiration())
                .statut(r.getStatut() != null ? StatutBus.valueOf(r.getStatut().toUpperCase()) : StatutBus.OPERATIONNEL)
                .build();
        return busRepository.save(bus);
    }

    @Override
    public Page<Bus> listerTous(int page, int size, String statut) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("immatriculation").ascending());
        if (statut != null && !statut.isBlank()) {
            return busRepository.findByStatut(StatutBus.valueOf(statut), pageable);
        }
        return busRepository.findAll(pageable);
    }

    @Override
    public Bus obtenirParId(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Bus introuvable : " + id));
    }

    @Override
    public Bus modifier(Long id, BusRequest r) {
        Bus bus = obtenirParId(id);
        if (r.getModele() != null) bus.setModele(r.getModele());
        if (r.getNombrePlaces() != null) bus.setNombrePlaces(r.getNombrePlaces());
        if (r.getLigneAssigneeId() != null) bus.setLigneAssignee(resoudreLigne(r.getLigneAssigneeId()));
        if (r.getProchainEntretien() != null) bus.setProchainEntretien(r.getProchainEntretien());
        if (r.getAssuranceExpiration() != null) bus.setAssuranceExpiration(r.getAssuranceExpiration());
        if (r.getStatut() != null) bus.setStatut(StatutBus.valueOf(r.getStatut().toUpperCase()));
        return busRepository.save(bus);
    }

    @Override
    public void supprimer(Long id) {
        Bus bus = obtenirParId(id);
        List<Voyage> voyages = voyageRepository.findByBus_Id(id);
        List<Voyage> voyagesBloquants = voyages.stream()
                .filter(v -> v.getStatut() == StatutVoyage.PLANIFIE
                        || v.getStatut() == StatutVoyage.EN_COURS
                        || v.getStatut() == StatutVoyage.EN_ROUTE)
                .toList();
        if (!voyagesBloquants.isEmpty()) {
            throw new RegleMetierException("Impossible de supprimer ce bus : "
                    + voyagesBloquants.size()
                    + " voyage(s) actif(s) (PLANIFIE ou EN_COURS) le référence encore.");
        }
        busRepository.delete(bus);
    }

    private Ligne resoudreLigne(Long ligneId) {
        if (ligneId == null) return null;
        return ligneRepository.findById(ligneId)
                .orElseThrow(() -> new RessourceIntrouvableException("Ligne introuvable : " + ligneId));
    }
}
