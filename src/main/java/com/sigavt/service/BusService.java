package com.sigavt.service;

import com.sigavt.dto.request.BusRequest;
import com.sigavt.entity.Bus;
import org.springframework.data.domain.Page;

public interface BusService {
    Bus creer(BusRequest request);
    Page<Bus> listerTous(int page, int size, String statut);
    Bus obtenirParId(Long id);
    Bus modifier(Long id, BusRequest request);
    void supprimer(Long id);
}
