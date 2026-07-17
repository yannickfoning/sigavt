package com.sigavt.service;

import com.sigavt.entity.Agence;
import org.springframework.data.domain.Page;

public interface AgenceService {
    Agence creer(Agence agence);
    Page<Agence> listerToutes(int page, int size, String search);
    Agence obtenirParId(Long id);
    Agence modifier(Long id, Agence agence);
    void supprimer(Long id);
}
