package com.microservice.notification.notification_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an error response returned by the service when an error occurs.
 * <p>
 * This class encapsulates the status code and the error message to be sent back to the client in case of an error.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * The HTTP status code indicating the type of error (e.g., 400 for bad request, 404 for not found).
     */
    private int statusCode;

    /**
     * A detailed error message describing the cause of the failure.
     */
    private String errorMessage;
}
