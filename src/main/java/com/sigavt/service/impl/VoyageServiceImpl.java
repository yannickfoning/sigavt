package com.sigavt.service.impl;

import com.sigavt.dto.request.VoyageRequest;
import com.sigavt.entity.*;
import com.sigavt.enums.StatutVoyage;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.*;
import com.sigavt.service.VoyageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoyageServiceImpl implements VoyageService {

    private final VoyageRepository voyageRepository;
    private final LigneRepository ligneRepository;
    private final BusRepository busRepository;
    private final PersonnelRepository personnelRepository;
    private final SiegeRepository siegeRepository;

    @Override
    public Voyage creer(VoyageRequest r) {
        Ligne ligne = ligneRepository.findById(r.getLigneId())
                .orElseThrow(() -> new RessourceIntrouvableException("Ligne introuvable : " + r.getLigneId()));
        Bus bus = r.getBusId() != null
                ? busRepository.findById(r.getBusId()).orElseThrow(() -> new RessourceIntrouvableException("Bus introuvable"))
                : null;
        Personnel chauffeur = r.getChauffeurId() != null
                ? personnelRepository.findById(r.getChauffeurId()).orElseThrow(() -> new RessourceIntrouvableException("Chauffeur introuvable"))
                : null;

        int places = r.getPlacesDisponibles() != null ? r.getPlacesDisponibles()
                : (bus != null ? bus.getNombrePlaces() : 0);

        Voyage voyage = Voyage.builder()
                .ligne(ligne)
                .bus(bus)
                .chauffeur(chauffeur)
                .dateVoyage(r.getDateVoyage())
                .heureDepart(r.getHeureDepart())
                .placesDisponibles(places)
                .statut(r.getStatut() != null ? StatutVoyage.valueOf(r.getStatut()) : StatutVoyage.PLANIFIE)
                .build();
        voyage = voyageRepository.save(voyage);

        // Generation automatique du plan de sieges (layout 2+2)
        if (bus != null) {
            genererSieges(voyage, bus.getNombrePlaces());
        }
        return voyage;
    }

    private void genererSieges(Voyage voyage, int nombrePlaces) {
        String[] lettres = {"A", "B", "C", "D"};
        int rangee = 1;
        int compte = 0;
        while (compte < nombrePlaces) {
            for (String lettre : lettres) {
                if (compte >= nombrePlaces) break;
                Siege siege = Siege.builder()
                        .voyage(voyage)
                        .numero(rangee + lettre)
                        .build();
                siegeRepository.save(siege);
                compte++;
            }
            rangee++;
        }
    }

    @Override
    public List<Voyage> listerParDate(LocalDate date) {
        return date != null ? voyageRepository.findByDateVoyage(date) : voyageRepository.findAll();
    }

    @Override
    public Page<Voyage> listerTous(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateVoyage").descending());
        return voyageRepository.findAll(pageable);
    }

    @Override
    public Voyage obtenirParId(Long id) {
        return voyageRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Voyage introuvable : " + id));
    }

    @Override
    public Voyage modifier(Long id, VoyageRequest r) {
        Voyage voyage = obtenirParId(id);
        if (r.getBusId() != null) {
            voyage.setBus(busRepository.findById(r.getBusId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Bus introuvable")));
        }
        if (r.getChauffeurId() != null) {
            voyage.setChauffeur(personnelRepository.findById(r.getChauffeurId())
                    .orElseThrow(() -> new RessourceIntrouvableException("Chauffeur introuvable")));
        }
        voyage.setDateVoyage(r.getDateVoyage());
        voyage.setHeureDepart(r.getHeureDepart());
        if (r.getPlacesDisponibles() != null) voyage.setPlacesDisponibles(r.getPlacesDisponibles());
        if (r.getStatut() != null) voyage.setStatut(StatutVoyage.valueOf(r.getStatut()));
        return voyageRepository.save(voyage);
    }

    @Override
    public void supprimer(Long id) {
        voyageRepository.delete(obtenirParId(id));
    }
}
