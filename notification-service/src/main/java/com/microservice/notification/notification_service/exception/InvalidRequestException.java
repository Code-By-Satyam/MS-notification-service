package com.microservice.notification.notification_service.exception;

/**
 * Custom exception class used to represent invalid requests within the notification service.
 * <p>
 * This exception is thrown when the input or request data is found to be invalid,
 * such as missing required fields, incorrect format, or other issues that prevent processing.
 * </p>
 */
public class InvalidRequestException extends RuntimeException {

    /**
     * Constructs a new InvalidRequestException with the specified error message.
     * <p>
     * This constructor is used to create an exception with a custom message indicating
     * the specific reason for the invalid request.
     * </p>
     *
     * @param errorMessage The detailed error message explaining why the request is invalid.
     */
    public InvalidRequestException(String errorMessage) {
        super(errorMessage);
    }
}
