package com.recipefinder.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
// Order - earlier
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class CustomSessionFixationCorsFilter extends OncePerRequestFilter {

//not in spring bean
private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

    System.out.println("CSFCF - Request URI: " + request.getRequestURI());
    System.out.println("CSFCF - Current JSESSIONID (from request): " + request.getRequestedSessionId());

    // manipulating request/response
    HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);

    // holder for securityContextRepository to read cookies
    SecurityContext contextBefore = securityContextRepository.loadContext(holder);

    Authentication authenticationBefore = contextBefore.getAuthentication();
    if (authenticationBefore != null && authenticationBefore.isAuthenticated() && !(authenticationBefore instanceof AnonymousAuthenticationToken)) {
        SecurityContextHolder.setContext(contextBefore);
        System.out.println("CSFCF - SecurityContext restored from session: " + authenticationBefore.getName());
    } else {
        System.out.println("CSFCF - No authenticated SecurityContext found in session.");
    }

    filterChain.doFilter(holder.getRequest(), holder.getResponse());

    // save context if worked on
    SecurityContext contextAfter = SecurityContextHolder.getContext();
    if (contextAfter != null && contextAfter.getAuthentication() != null && contextAfter.getAuthentication().isAuthenticated()) {
        securityContextRepository.saveContext(contextAfter, holder.getRequest(), holder.getResponse());
        System.out.println("CSFCF - SecurityContext saved to session for user: " + contextAfter.getAuthentication().getName());
    } else {
        System.out.println("CSFCF - No authenticated SecurityContext to save.");
    }
}
}