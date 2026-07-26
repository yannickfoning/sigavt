package com.sigavt.service.impl;

import com.sigavt.dto.request.LigneRequest;
import com.sigavt.entity.Ligne;
import com.sigavt.enums.StatutLigne;
import com.sigavt.exception.RegleMetierException;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.BusRepository;
import com.sigavt.repository.LigneRepository;
import com.sigavt.repository.VoyageRepository;
import com.sigavt.service.LigneService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LigneServiceImpl implements LigneService {

    private final LigneRepository ligneRepository;
    private final BusRepository busRepository;
    private final VoyageRepository voyageRepository;

    @Override
    public Ligne creer(LigneRequest r) {
        Ligne ligne = Ligne.builder()
                .villeDepart(r.getVilleDepart())
                .villeArrivee(r.getVilleArrivee())
                .distanceKm(r.getDistanceKm())
                .dureeMinutes(r.getDureeMinutes())
                .tarifBase(r.getTarifBase())
                .frequenceJour(r.getFrequenceJour())
                .statut(r.getStatut() != null ? StatutLigne.valueOf(r.getStatut().toUpperCase()) : StatutLigne.ACTIVE)
                .build();
        return ligneRepository.save(ligne);
    }

    @Override
    public Page<Ligne> listerToutes(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("villeDepart").ascending());
        if (search != null && !search.isBlank()) {
            return ligneRepository.searchLignes(search.trim(), pageable);
        }
        return ligneRepository.findAll(pageable);
    }

    @Override
    public Ligne obtenirParId(Long id) {
        return ligneRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Ligne introuvable : " + id));
    }

    @Override
    public Ligne modifier(Long id, LigneRequest r) {
        Ligne ligne = obtenirParId(id);
        if (r.getVilleDepart() != null) ligne.setVilleDepart(r.getVilleDepart());
        if (r.getVilleArrivee() != null) ligne.setVilleArrivee(r.getVilleArrivee());
        if (r.getDistanceKm() != null) ligne.setDistanceKm(r.getDistanceKm());
        if (r.getDureeMinutes() != null) ligne.setDureeMinutes(r.getDureeMinutes());
        if (r.getTarifBase() != null) ligne.setTarifBase(r.getTarifBase());
        if (r.getFrequenceJour() != null) ligne.setFrequenceJour(r.getFrequenceJour());
        if (r.getStatut() != null) ligne.setStatut(StatutLigne.valueOf(r.getStatut().toUpperCase()));
        return ligneRepository.save(ligne);
    }

    @Override
    public void supprimer(Long id) {
        Ligne ligne = obtenirParId(id);
        int busReferences = busRepository.findByLigneAssignee_Id(id).size();
        int voyagesReferences = voyageRepository.findByLigne_Id(id).size();
        if (busReferences > 0 || voyagesReferences > 0) {
            throw new RegleMetierException("Impossible de supprimer cette ligne : "
                    + busReferences + " bus et " + voyagesReferences + " voyage(s) la referencent encore.");
        }
        ligneRepository.delete(ligne);
    }
}
