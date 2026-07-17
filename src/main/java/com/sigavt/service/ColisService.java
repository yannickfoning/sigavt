package com.sigavt.service;

import com.sigavt.dto.request.ColisRequest;
import com.sigavt.entity.Colis;
import org.springframework.data.domain.Page;

public interface ColisService {
    Colis creer(ColisRequest request, String emailAgent);
    Colis obtenirParId(Long id);
    Colis obtenirParTracking(String numeroTracking);
    Page<Colis> listerTous(int page, int size, String statut);
    Colis mettreAJourStatut(Long id, String nouveauStatut);
    void supprimer(Long id);
}
