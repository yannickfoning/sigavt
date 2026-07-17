package com.sigavt.service;

import com.sigavt.dto.request.VoyageRequest;
import com.sigavt.entity.Voyage;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface VoyageService {
    Voyage creer(VoyageRequest request);
    List<Voyage> listerParDate(LocalDate date);
    Page<Voyage> listerTous(int page, int size);
    Voyage obtenirParId(Long id);
    Voyage modifier(Long id, VoyageRequest request);
    void supprimer(Long id);
}
