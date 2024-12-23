package com.microservice.notification.notification_service.controllers;

import com.microservice.notification.notification_service.entities.AuthRequest;
import com.microservice.notification.notification_service.services.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling authentication-related operations.
 * <p>
 * Provides an endpoint to generate a JWT token for the provided username.
 * </p>
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private JwtService jwtService;

    /**
     * Endpoint to generate an authentication token (JWT) for a given username.
     *
     * @param authRequest The authentication request containing the username.
     * @return A JWT token as a string.
     */
    @PostMapping("/authenticate")
    public String generateAuthToken(@RequestBody AuthRequest authRequest) {
        logger.info("Authentication request received for username: {}", authRequest.getUsername());
        try {
            String token = jwtService.generateToken(authRequest.getUsername());
            logger.info("JWT token generated successfully for username: {}", authRequest.getUsername());
            return token;
        } catch (Exception e) {
            logger.error("Error occurred while generating token for username: {}", authRequest.getUsername(), e);
            throw new RuntimeException("Token generation failed. Please try again.");
        }
    }
}
