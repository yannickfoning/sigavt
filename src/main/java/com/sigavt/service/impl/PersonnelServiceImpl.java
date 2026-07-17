package com.sigavt.service.impl;

import com.sigavt.dto.request.PersonnelRequest;
import com.sigavt.entity.Agence;
import com.sigavt.entity.Bus;
import com.sigavt.entity.Personnel;
import com.sigavt.enums.Poste;
import com.sigavt.enums.StatutEmploye;
import com.sigavt.enums.TypeContrat;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.AgenceRepository;
import com.sigavt.repository.BusRepository;
import com.sigavt.repository.PersonnelRepository;
import com.sigavt.service.PersonnelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PersonnelServiceImpl implements PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final BusRepository busRepository;
    private final AgenceRepository agenceRepository;

    @Override
    public Personnel creer(PersonnelRequest r) {
        Personnel p = Personnel.builder()
                .nomComplet(r.getNomComplet())
                .telephone(r.getTelephone())
                .poste(Poste.valueOf(r.getPoste()))
                .typeContrat(r.getTypeContrat() != null ? TypeContrat.valueOf(r.getTypeContrat()) : TypeContrat.CDI)
                .dateFinContrat(r.getDateFinContrat())
                .salaireBase(r.getSalaireBase())
                .numeroCnps(r.getNumeroCnps())
                .numeroCni(r.getNumeroCni())
                .permisConduire(r.getPermisConduire())
                .busAssigne(resoudreBus(r.getBusAssigneId()))
                .agence(resoudreAgence(r.getAgenceId()))
                .statut(r.getStatut() != null ? StatutEmploye.valueOf(r.getStatut()) : StatutEmploye.ACTIF)
                .dateEmbauche(r.getDateEmbauche())
                .build();
        return personnelRepository.save(p);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Personnel> listerTous(int page, int size, String poste, String recherche) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateEmbauche").descending());
        if (StringUtils.hasText(recherche)) {
            return personnelRepository.findByNomCompletContainingIgnoreCase(recherche.trim(), pageable);
        }
        if (StringUtils.hasText(poste)) {
            return personnelRepository.findByPoste(Poste.valueOf(poste), pageable);
        }
        return personnelRepository.findAll(pageable);
    }

    @Override
    public Personnel obtenirParId(Long id) {
        return personnelRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Employe introuvable : " + id));
    }

    @Override
    public Personnel modifier(Long id, PersonnelRequest r) {
        Personnel p = obtenirParId(id);
        p.setNomComplet(r.getNomComplet());
        p.setTelephone(r.getTelephone());
        p.setPoste(Poste.valueOf(r.getPoste()));
        if (r.getTypeContrat() != null) p.setTypeContrat(TypeContrat.valueOf(r.getTypeContrat()));
        p.setDateFinContrat(r.getDateFinContrat());
        p.setSalaireBase(r.getSalaireBase());
        p.setNumeroCnps(r.getNumeroCnps());
        p.setNumeroCni(r.getNumeroCni());
        p.setPermisConduire(r.getPermisConduire());
        p.setBusAssigne(resoudreBus(r.getBusAssigneId()));
        p.setAgence(resoudreAgence(r.getAgenceId()));
        if (r.getStatut() != null) p.setStatut(StatutEmploye.valueOf(r.getStatut()));
        p.setDateEmbauche(r.getDateEmbauche());
        return personnelRepository.save(p);
    }

    @Override
    public void supprimer(Long id) {
        personnelRepository.delete(obtenirParId(id));
    }

    private Bus resoudreBus(Long busId) {
        if (busId == null) return null;
        return busRepository.findById(busId)
                .orElseThrow(() -> new RessourceIntrouvableException("Bus introuvable : " + busId));
    }

    private Agence resoudreAgence(Long agenceId) {
        if (agenceId == null) return null;
        return agenceRepository.findById(agenceId)
                .orElseThrow(() -> new RessourceIntrouvableException("Agence introuvable : " + agenceId));
    }
}
