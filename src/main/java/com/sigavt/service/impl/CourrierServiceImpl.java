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
        if (r.getType() == null || r.getType().isBlank()) {
            throw new com.sigavt.exception.RegleMetierException("Le type de courrier est obligatoire");
        }
        if (r.getObjet() == null || r.getObjet().isBlank()) {
            throw new com.sigavt.exception.RegleMetierException("L'objet du courrier est obligatoire");
        }
        String numeroCourrier = genererNumeroCourrier();
        Courrier c = Courrier.builder()
                .numeroCourrier(numeroCourrier)
                .typeCourrier(TypeCourrier.valueOf(r.getType().toUpperCase()))
                .objet(r.getObjet())
                .expediteur(r.getExpediteur())
                .destinataire(r.getDestinataire())
                .dateReception(r.getDateReception() != null ? r.getDateReception() : LocalDate.now())
                .statut(StatutCourrier.NON_LU)
                .observations(r.getObservations())
                .build();
        return courrierRepository.save(c);
    }

    private String genererNumeroCourrier() {
        String prefix = "C-" + LocalDate.now().getYear();
        long count = courrierRepository.count() + 1;
        return String.format("%s-%04d", prefix, count);
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
        if (r.getStatut() != null) c.setStatut(StatutCourrier.valueOf(r.getStatut().toUpperCase()));
        return courrierRepository.save(c);
    }

    @Override
    public void supprimer(Long id) {
        courrierRepository.delete(obtenirParId(id));
    }
}
