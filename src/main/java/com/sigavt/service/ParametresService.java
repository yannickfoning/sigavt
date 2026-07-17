package com.sigavt.service;

import com.sigavt.dto.request.ParametresAgenceRequest;
import com.sigavt.entity.ParametresAgence;

public interface ParametresService {
    ParametresAgence obtenir();
    ParametresAgence mettreAJour(ParametresAgenceRequest request);
}