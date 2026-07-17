package com.sigavt.service;

import com.sigavt.dto.request.BilletRequest;
import com.sigavt.entity.Billet;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface BilletService {
    Billet vendre(BilletRequest request, String emailAgent);
    List<Billet> listerParVoyage(Long voyageId);
    List<Billet> listerParDate(LocalDate date);
    Page<Billet> listerTous(int page, int size, String search, String statut);
    Billet obtenirParId(Long id);
    Billet annuler(Long id);
}
