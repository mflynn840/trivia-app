package com.example.spring_boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // all endpoints
                        .allowedOriginPatterns("http://localhost:5173") // your frontend origin
                        .allowCredentials(true) // allow Authorization headers/cookies
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS"); // allowed HTTP methods
            }
        };
    }
}
