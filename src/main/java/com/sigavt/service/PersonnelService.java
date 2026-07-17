package com.sigavt.service;

import com.sigavt.dto.request.PersonnelRequest;
import com.sigavt.entity.Personnel;
import org.springframework.data.domain.Page;

public interface PersonnelService {
    Personnel creer(PersonnelRequest request);
    Page<Personnel> listerTous(int page, int size, String poste, String recherche);
    Personnel obtenirParId(Long id);
    Personnel modifier(Long id, PersonnelRequest request);
    void supprimer(Long id);
}
