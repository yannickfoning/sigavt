package com.sigavt.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErreurReponse {
    private LocalDateTime horodatage;
    private int statut;
    private String erreur;
    private String message;
    private Map<String, String> details;
}
