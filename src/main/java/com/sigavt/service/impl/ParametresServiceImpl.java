package com.sigavt.service.impl;

import com.sigavt.dto.request.ParametresAgenceRequest;
import com.sigavt.entity.ParametresAgence;
import com.sigavt.repository.ParametresAgenceRepository;
import com.sigavt.service.ParametresService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParametresServiceImpl implements ParametresService {

    private final ParametresAgenceRepository parametresAgenceRepository;

    @Override
    public ParametresAgence obtenir() {
        return parametresAgenceRepository.findAll().stream().findFirst()
                .orElseGet(() -> parametresAgenceRepository.save(
                        ParametresAgence.builder()
                                .nomAgence("Agence Voyage CM")
                                .villePrincipale("Yaounde")
                                .build()
                ));
    }

    @Override
    public ParametresAgence mettreAJour(ParametresAgenceRequest r) {
        ParametresAgence p = obtenir();
        p.setNomAgence(r.getNomAgence());
        p.setTelephone(r.getTelephone());
        p.setEmail(r.getEmail());
        p.setVillePrincipale(r.getVillePrincipale());
        p.setAdresse(r.getAdresse());
        return parametresAgenceRepository.save(p);
    }
}
