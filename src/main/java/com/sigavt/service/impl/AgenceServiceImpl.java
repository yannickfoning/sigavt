package com.sigavt.service.impl;

import com.sigavt.entity.Agence;
import com.sigavt.exception.RessourceIntrouvableException;
import com.sigavt.repository.AgenceRepository;
import com.sigavt.service.AgenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgenceServiceImpl implements AgenceService {

    private final AgenceRepository agenceRepository;

    @Override
    public Agence creer(Agence agence) {
        return agenceRepository.save(agence);
    }

    @Override
    public Page<Agence> listerToutes(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nom").ascending());
        if (search != null && !search.isBlank()) {
            return agenceRepository.searchAgences(search.trim(), pageable);
        }
        return agenceRepository.findAll(pageable);
    }

    @Override
    public Agence obtenirParId(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Agence introuvable : " + id));
    }

    @Override
    public Agence modifier(Long id, Agence agence) {
        Agence existante = obtenirParId(id);
        existante.setNom(agence.getNom());
        existante.setVille(agence.getVille());
        existante.setAdresse(agence.getAdresse());
        existante.setTelephone(agence.getTelephone());
        existante.setEmail(agence.getEmail());
        return agenceRepository.save(existante);
    }

    @Override
    public void supprimer(Long id) {
        agenceRepository.delete(obtenirParId(id));
    }
}
