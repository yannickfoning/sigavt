package com.sigavt.service.impl;

import com.sigavt.dto.response.AlerteResponse;
import com.sigavt.dto.response.DashboardResponse;
import com.sigavt.entity.Billet;
import com.sigavt.entity.Bus;
import com.sigavt.entity.Colis;
import com.sigavt.entity.Voyage;
import com.sigavt.enums.StatutBus;
import com.sigavt.enums.StatutColis;
import com.sigavt.repository.*;
import com.sigavt.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final VoyageRepository voyageRepository;
    private final BusRepository busRepository;
    private final ColisRepository colisRepository;
    private final BilletRepository billetRepository;

    @Override
    public DashboardResponse obtenirTableauDeBord() {
        LocalDate aujourdHui = LocalDate.now();
        List<Voyage> voyagesDuJour = voyageRepository.findByDateVoyage(aujourdHui);

        long busEnService = busRepository.findByStatut(StatutBus.OPERATIONNEL).size()
                + busRepository.findByStatut(StatutBus.EN_ROUTE).size()
                + busRepository.findByStatut(StatutBus.EMBARQUEMENT).size();
        long busTotal = busRepository.count();

        List<Colis> colisEnTransit = colisRepository.findByStatut(StatutColis.EN_TRANSIT);

        long billetsAujourdHui = 0;
        BigDecimal recettesAujourdHui = BigDecimal.ZERO;
        for (Voyage v : voyagesDuJour) {
            List<Billet> billets = billetRepository.findByVoyage_Id(v.getId());
            billetsAujourdHui += billets.size();
            for (Billet b : billets) {
                recettesAujourdHui = recettesAujourdHui.add(b.getPrix());
            }
        }

        List<AlerteResponse> alertes = construireAlertes();

        return DashboardResponse.builder()
                .voyagesAujourdHui(voyagesDuJour.size())
                .busEnService(busEnService)
                .busTotal(busTotal)
                .colisEnTransit(colisEnTransit.size())
                .billetsVendusAujourdHui(billetsAujourdHui)
                .recettesAujourdHui(recettesAujourdHui)
                .alertes(alertes)
                .build();
    }

    private List<AlerteResponse> construireAlertes() {
        List<AlerteResponse> alertes = new ArrayList<>();
        LocalDate seuilEntretien = LocalDate.now().plusDays(10);

        for (Bus bus : busRepository.findAll()) {
            if (bus.getProchainEntretien() != null && bus.getProchainEntretien().isBefore(seuilEntretien)) {
                alertes.add(AlerteResponse.builder()
                        .type("VISITE_TECHNIQUE")
                        .titre("Visite technique a renouveler")
                        .details("Bus " + bus.getImmatriculation() + " - echeance " + bus.getProchainEntretien())
                        .niveau("ROUGE")
                        .build());
            }
            if (bus.getAssuranceExpiration() != null && bus.getAssuranceExpiration().isBefore(LocalDate.now())) {
                alertes.add(AlerteResponse.builder()
                        .type("ASSURANCE")
                        .titre("Assurance expiree")
                        .details("Bus " + bus.getImmatriculation() + " - a renouveler immediatement")
                        .niveau("ORANGE")
                        .build());
            }
        }

        long colisNonReclames = colisRepository.findByStatut(StatutColis.NON_RECLAME).size();
        if (colisNonReclames > 0) {
            alertes.add(AlerteResponse.builder()
                    .type("COLIS_NON_RECLAME")
                    .titre("Colis non reclames")
                    .details(colisNonReclames + " colis en attente depuis plus de 7 jours")
                    .niveau("BLEU")
                    .build());
        }
        return alertes;
    }

    @Override
    public List<?> getDeparts(LocalDate date) {
        List<Voyage> voyages = voyageRepository.findByDateVoyage(date);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Voyage v : voyages) {
            Map<String, Object> voyageMap = new HashMap<>();
            voyageMap.put("id", v.getId());
            voyageMap.put("date", v.getDateVoyage());
            voyageMap.put("heure", v.getHeureDepart());
            voyageMap.put("places", v.getPlacesDisponibles());
            voyageMap.put("statut", v.getStatut());
            if (v.getLigne() != null) {
                voyageMap.put("depart", v.getLigne().getVilleDepart());
                voyageMap.put("arrivee", v.getLigne().getVilleArrivee());
            }
            if (v.getBus() != null) {
                voyageMap.put("bus", v.getBus().getImmatriculation());
            }
            result.add(voyageMap);
        }
        return result;
    }

    @Override
    public List<?> getRecettesSemaine() {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        String[] jours = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};

        for (int i = 6; i >= 0; i--) {
            LocalDate jour = today.minusDays(i);

            BigDecimal recettes = BigDecimal.ZERO;
            List<Voyage> voyages = voyageRepository.findByDateVoyage(jour);
            for (Voyage v : voyages) {
                List<Billet> billets = billetRepository.findByVoyage_Id(v.getId());
                for (Billet b : billets) {
                    recettes = recettes.add(b.getPrix());
                }
            }

            Map<String, Object> jourMap = new HashMap<>();
            jourMap.put("jour", jour.toString());
            jourMap.put("jourCourt", jours[jour.getDayOfWeek().getValue() - 1]);
            jourMap.put("montant", recettes);
            result.add(jourMap);
        }
        return result;
    }

    @Override
    public List<?> getTopLignes() {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Voyage> allVoyages = voyageRepository.findAll();
        Map<String, BigDecimal> ligneRecettes = new HashMap<>();

        for (Voyage v : allVoyages) {
            if (v.getLigne() != null) {
                String ligneKey = v.getLigne().getVilleDepart() + " - " + v.getLigne().getVilleArrivee();
                BigDecimal recettes = BigDecimal.ZERO;
                List<Billet> billets = billetRepository.findByVoyage_Id(v.getId());
                for (Billet b : billets) {
                    recettes = recettes.add(b.getPrix());
                }
                ligneRecettes.merge(ligneKey, recettes, BigDecimal::add);
            }
        }

        ligneRecettes.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(entry -> {
                    Map<String, Object> ligneMap = new HashMap<>();
                    ligneMap.put("ligne", entry.getKey());
                    ligneMap.put("recettes", entry.getValue());
                    result.add(ligneMap);
                });

        return result;
    }

    @Override
    public List<?> getAlertes() {
        return construireAlertes();
    }
}
