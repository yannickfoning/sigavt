package com.sigavt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ErreurReponse> handleNotFound(RessourceIntrouvableException ex) {
        return construire(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<ErreurReponse> handleRegleMetier(RegleMetierException ex) {
        return construire(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErreurReponse> handleBadCredentials(BadCredentialsException ex) {
        return construire(HttpStatus.UNAUTHORIZED, "Email ou mot de passe incorrect", null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurReponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return construire(HttpStatus.BAD_REQUEST, "Corps de la requête invalide : JSON malformé", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurReponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            details.put(fe.getField(), fe.getDefaultMessage());
        }
        return construire(HttpStatus.BAD_REQUEST, "Erreur de validation", details);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErreurReponse> handleAccessDenied(AccessDeniedException ex) {
        return construire(HttpStatus.FORBIDDEN, "Accès refusé : vous n'avez pas les droits nécessaires", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErreurReponse> handleIllegalArgument(IllegalArgumentException ex) {
        return construire(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurReponse> handleGeneric(Exception ex) {
        Map<String, String> details = new HashMap<>();
        details.put("exception", ex.getClass().getSimpleName());
        details.put("message", ex.getMessage());
        return construire(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne est survenue. Veuillez contacter l'administrateur.", details);
    }

    private ResponseEntity<ErreurReponse> construire(HttpStatus statut, String message, Map<String, String> details) {
        ErreurReponse body = ErreurReponse.builder()
                .horodatage(LocalDateTime.now())
                .statut(statut.value())
                .erreur(statut.getReasonPhrase())
                .message(message)
                .details(details)
                .build();
        return ResponseEntity.status(statut).body(body);
    }
}
