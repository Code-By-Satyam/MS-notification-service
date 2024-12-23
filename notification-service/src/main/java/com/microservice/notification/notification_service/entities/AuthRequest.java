package com.microservice.notification.notification_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a request to authenticate a user by providing their credentials (username and password).
 * <p>
 * This class is used to encapsulate the authentication request details sent to the authentication endpoint.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    /**
     * The username of the user trying to authenticate.
     */
    private String username;

    /**
     * The password of the user trying to authenticate.
     */
    private String password;
}
