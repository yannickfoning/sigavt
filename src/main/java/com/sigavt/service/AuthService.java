package com.sigavt.service;

import com.sigavt.dto.request.LoginRequest;
import com.sigavt.dto.request.UtilisateurRequest;
import com.sigavt.dto.response.LoginResponse;
import com.sigavt.entity.Utilisateur;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    Utilisateur inscrire(UtilisateurRequest request);
    Utilisateur inscrirePublic(UtilisateurRequest request);
    Utilisateur creerAvecRole(UtilisateurRequest request);
}
