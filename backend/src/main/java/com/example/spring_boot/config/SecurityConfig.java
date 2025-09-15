package com.example.spring_boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // usually disabled for APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/questions/**").authenticated()
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()                
            )
            .formLogin(AbstractHttpConfigurer::disable)  // no redirect to login page
            .httpBasic(AbstractHttpConfigurer::disable); // no browser popup auth

        return http.build();
    }
}


