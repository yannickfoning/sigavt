package com.sigavt.service.impl;

import com.sigavt.dto.request.CourrierRequest;
import com.sigavt.entity.Courrier;
import com.sigavt.enums.StatutCourrier;
import com.sigavt.enums.TypeCourrier;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.CourrierRepository;
import com.sigavt.service.CourrierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourrierServiceImpl implements CourrierService {

    private final CourrierRepository courrierRepository;

    @Override
    public Courrier creer(CourrierRequest r) {
        Courrier c = Courrier.builder()
                .typeCourrier(TypeCourrier.valueOf(r.getType()))
                .objet(r.getObjet())
                .expediteur(r.getExpediteur())
                .destinataire(r.getDestinataire())
                .dateReception(r.getDateReception() != null ? r.getDateReception() : LocalDate.now())
                .statut(StatutCourrier.NON_LU)
                .observations(r.getObservations())
                .build();
        return courrierRepository.save(c);
    }

    @Override
    public Courrier obtenirParId(Long id) {
        return courrierRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Courrier introuvable : " + id));
    }

    @Override
    public List<Courrier> listerTous() {
        return courrierRepository.findAll();
    }

    @Override
    public Courrier modifier(Long id, CourrierRequest r) {
        Courrier c = obtenirParId(id);
        if (r.getObjet() != null) c.setObjet(r.getObjet());
        if (r.getExpediteur() != null) c.setExpediteur(r.getExpediteur());
        if (r.getDestinataire() != null) c.setDestinataire(r.getDestinataire());
        if (r.getStatut() != null) c.setStatut(StatutCourrier.valueOf(r.getStatut()));
        return courrierRepository.save(c);
    }

    @Override
    public void supprimer(Long id) {
        courrierRepository.delete(obtenirParId(id));
    }
}
