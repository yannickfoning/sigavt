package com.sigavt.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String handleErrorHtml(HttpServletRequest request, Model model) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (status == null) status = 500;
        model.addAttribute("status", status);
        model.addAttribute("message", getMessageForStatus(status));
        return "error";
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> handleErrorJson(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (status == null) status = 500;
        return ResponseEntity.status(status).body(Map.of(
            "status", status,
            "error", getMessageForStatus(status)
        ));
    }

    private String getMessageForStatus(int status) {
        switch (status) {
            case 400: return "Requête invalide";
            case 401: return "Non authentifié. Veuillez vous connecter.";
            case 403: return "Accès refusé. Droits insuffisants.";
            case 404: return "Ressource introuvable.";
            case 405: return "Méthode HTTP non autorisée.";
            case 500: return "Erreur interne du serveur.";
            default: return "Une erreur inattendue s'est produite.";
        }
    }
}
