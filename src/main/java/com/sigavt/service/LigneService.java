package com.sigavt.service;

import com.sigavt.dto.request.LigneRequest;
import com.sigavt.entity.Ligne;
import org.springframework.data.domain.Page;

public interface LigneService {
    Ligne creer(LigneRequest request);
    Page<Ligne> listerToutes(int page, int size, String search);
    Ligne obtenirParId(Long id);
    Ligne modifier(Long id, LigneRequest request);
    void supprimer(Long id);
}
