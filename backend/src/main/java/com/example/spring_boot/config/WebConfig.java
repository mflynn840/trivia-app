package com.example.spring_boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration for Spring Boot application.
 * This allows cross-origin requests from the Android app over the LAN.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // Configure CORS for your Android app
        registry.addMapping("/**") // Apply to all endpoints
                .allowedOrigins("http://192.168.x.x")  // Replace with your Android app's LAN IP
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Allow standard HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // Allow credentials (cookies, headers, etc.)
    }
}