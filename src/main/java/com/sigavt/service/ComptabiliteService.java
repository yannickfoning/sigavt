package com.sigavt.service;

import com.sigavt.dto.request.EcritureComptableRequest;
import com.sigavt.entity.EcritureComptable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ComptabiliteService {
    EcritureComptable creer(EcritureComptableRequest request);
    EcritureComptable obtenirParId(Long id);
    List<EcritureComptable> listerParPeriode(LocalDate debut, LocalDate fin);
    Map<String, Object> genererBilan(int mois, int annee);
    void supprimer(Long id);
}