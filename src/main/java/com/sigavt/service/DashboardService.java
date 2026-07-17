package com.sigavt.service;

import com.sigavt.dto.response.DashboardResponse;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    DashboardResponse obtenirTableauDeBord();
    List<?> getDeparts(LocalDate date);
    List<?> getRecettesSemaine();
    List<?> getTopLignes();
    List<?> getAlertes();
}
