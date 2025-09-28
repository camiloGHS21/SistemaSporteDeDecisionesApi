package com.example.demo.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // Permite cualquier origen
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite los métodos HTTP comunes
                .allowedHeaders("*") // Permite cualquier encabezado
                .allowCredentials(false); // No permite credenciales (cookies, encabezados de autorización) con "*" en allowedOrigins
    }
}
