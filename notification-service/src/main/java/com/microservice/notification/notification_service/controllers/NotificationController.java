package com.microservice.notification.notification_service.controllers;

import com.google.gson.Gson;
import com.microservice.notification.notification_service.constants.NotificationConstants;
import com.microservice.notification.notification_service.entities.NotificationRequest;
import com.microservice.notification.notification_service.entities.NotificationResponse;
import com.microservice.notification.notification_service.exception.InvalidRequestException;
import com.microservice.notification.notification_service.services.NotificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing notifications.
 * <p>
 * Provides endpoints to publish notifications, check notification statuses, and perform a health check.
 * </p>
 */
@RestController
@RequestMapping("/notification")
public class NotificationController {

    private static final Logger logger = LogManager.getLogger(NotificationController.class);
    private final Gson gson = new Gson();

    @Autowired
    private NotificationService notificationService;

    /**
     * Endpoint to perform a health check for the notification service.
     *
     * @return A status string indicating the service is operational.
     */
    @GetMapping("/healthCheck")
    public String healthCheck() {
        logger.info("Health check endpoint hit");
        return NotificationConstants.OK;
    }

    /**
     * Endpoint to publish a new notification.
     *
     * @param notificationRequest The request payload containing notification details.
     * @return A response entity containing the notification response or an error message.
     */
    @PostMapping("/publish")
    public ResponseEntity<?> publishNotification(@RequestBody NotificationRequest notificationRequest) {
        logger.info("Request for notification received: {}", gson.toJson(notificationRequest));

        try {
            NotificationResponse response = notificationService.publishNotification(notificationRequest);
            logger.info("Notification published successfully: {}", gson.toJson(response));
            return ResponseEntity.ok(response);
        } catch (InvalidRequestException exception) {
            logger.error("Failed to publish notification: {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        } catch (Exception exception) {
            logger.error("Unexpected error occurred: {}", exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }

    /**
     * Endpoint to retrieve the status of a notification by its identifier.
     *
     * @param identifier The unique identifier of the notification.
     * @return A response entity containing the notification status or an error message.
     */
    @GetMapping("/statusById/{identifier}")
    public ResponseEntity<?> getStatusById(@PathVariable String identifier) {
        logger.info("Status check request received for identifier: {}", identifier);

        if (!StringUtils.hasText(identifier)) {
            logger.warn("Invalid identifier received for status check");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Request: Identifier cannot be null or empty.");
        }

        try {
            NotificationRequest notification = notificationService.getNotificationStatusById(identifier);
            logger.info("Notification status retrieved successfully for identifier: {}", identifier);
            return ResponseEntity.ok(notification);
        } catch (InvalidRequestException exception) {
            logger.warn("No notification found for identifier: {}", identifier);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        } catch (Exception exception) {
            logger.error("Unexpected error occurred while retrieving status: {}", exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again later.");
        }
    }
}
