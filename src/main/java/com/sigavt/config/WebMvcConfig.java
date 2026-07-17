package com.sigavt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // Spring Boot sert automatiquement les fichiers statiques depuis classpath:/static/
    // Pas besoin de configuration manuelle
}
