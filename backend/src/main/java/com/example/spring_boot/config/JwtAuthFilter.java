package com.example.spring_boot.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
        throws ServletException, IOException {

        //do not filter public endpoints
        if (request.getRequestURI().startsWith("/api/auth/") || "OPTIONS".equalsIgnoreCase(request.getMethod())){
            filterChain.doFilter(request, response);
            return;
        }

        //get the authorization header from the request
        final String authHeader = request.getHeader("Authorization");

        //no bearer token means go through normal filter chain
        if ( authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); 
            return;
        }

        //get the JWT token from the authorization header
        try{
            final String jwt = authHeader.substring(7);
            final String username = jwtUtil.getUsername(jwt);
            
            //Get the current authentication from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            // Only authenticate if
            //  1. The username was succesfully extracted from the token
            //  2. The current request is not already authenticated
            if (username != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);


                //validate the JWT token
                if (this.jwtUtil.isTokenValid(jwt, userDetails)) {
                    //create an authentication object for the user
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );                 

                    //set the users authentication in spring security context
                    // Allow the user to access their endpoints (restricted behind login)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
            filterChain.doFilter(request, response);
        }catch(Exception exception){
            this.handlerExceptionResolver.resolveException(request, response, null, exception);
        }
        
    }
}