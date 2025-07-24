package com.rentalapp.car_rental_system.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.error("Authentication failed: {}", exception.getMessage(), exception);


        Throwable cause = exception;
        while (cause != null) {
            log.debug("🔍 Cause chain: {}", cause.toString());
            if (cause.getMessage() != null && cause.getMessage().toLowerCase().contains("connection refused")) {
                log.warn("Detected DB connection issue in failure handler. Redirecting to /error-db");
                response.sendRedirect("/error-db");
                return;
            }
            cause = cause.getCause();
        }


        log.warn(" No DB issue detected. Redirecting to /login?error");
        response.sendRedirect("/login?error");
    }
}
