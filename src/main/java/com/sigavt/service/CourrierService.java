package com.sigavt.service;

import com.sigavt.dto.request.CourrierRequest;
import com.sigavt.entity.Courrier;

import java.util.List;

public interface CourrierService {
    Courrier creer(CourrierRequest request);
    Courrier obtenirParId(Long id);
    List<Courrier> listerTous();
    Courrier modifier(Long id, CourrierRequest request);
    void supprimer(Long id);
}