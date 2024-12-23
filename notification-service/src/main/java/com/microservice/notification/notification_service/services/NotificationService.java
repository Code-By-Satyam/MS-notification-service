package com.microservice.notification.notification_service.services;

import com.google.gson.Gson;
import com.microservice.notification.notification_service.constants.NotificationConstants;
import com.microservice.notification.notification_service.constants.NotificationStatusEnum;
import com.microservice.notification.notification_service.dao.NotificationRepository;
import com.microservice.notification.notification_service.entities.NotificationRequest;
import com.microservice.notification.notification_service.entities.NotificationResponse;
import com.microservice.notification.notification_service.exception.InvalidRequestException;
import com.microservice.notification.notification_service.producers.NotificationProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    private final NotificationProducer producer;
    private final NotificationRepository repository;
    private final Gson gson;

    public NotificationService(NotificationProducer producer, NotificationRepository repository) {
        this.producer = producer;
        this.repository = repository;
        this.gson = new Gson();
    }

    @CircuitBreaker(name = "notificationServiceCircuitBreaker", fallbackMethod = "publishNotificationFallback")
    public NotificationResponse publishNotification(NotificationRequest request) throws InvalidRequestException {
        if (new Random().nextBoolean()) {
            logger.error("Circuit breaker invoked due to simulated failure.");
            throw new RuntimeException("Simulated error to trigger circuit breaker.");
        }
        if (this.validate(request)) {
            String identifier = UUID.randomUUID().toString();
            request.setIdentifier(identifier);
            request.setStatus(NotificationStatusEnum.RECEIVED.name());
            // Log the request before processing
            logger.info("Publishing notification with identifier: {}", identifier);
            repository.save(request);
            // Send message to Kafka
            String topic = getTopicByChannel(request.getNotificationType());
            if (topic == null) {
                throw new InvalidRequestException("Invalid notification type: " + request.getNotificationType());
            }
            try {
                producer.sendMessage(gson.toJson(request), topic);
            } catch (Exception e) {
                logger.error("Error sending message to Kafka: {}", e.getMessage());
                // Optionally handle retries or fallback logic
                throw new RuntimeException("Error publishing notification message to Kafka.");
            }

            // Log successful notification processing
            logger.info("Notification successfully published with identifier: {}", identifier);

            return NotificationResponse.getResponse(request);
        } else {
            throw new InvalidRequestException("Invalid request object");
        }
    }

    public NotificationResponse publishNotificationFallback(NotificationRequest request, Exception ex) {
        logger.error("Circuit breaker triggered or service failure: {}", ex.getMessage(), ex);
        NotificationResponse notificationResponse = new NotificationResponse();
        notificationResponse.setStatus("We are experiencing a high load at the moment. Please try again later.");
        return notificationResponse;
    }

    public NotificationRequest getNotificationStatusById(String identifier) throws InvalidRequestException {
        return repository.findByIdentifier(identifier)
                .orElseThrow(() -> {
                    logger.warn("No notification found with identifier: {}", identifier);
                    return new InvalidRequestException("No notification found with this identifier.");
                });
    }

    private String getTopicByChannel(String channel) {
        switch (channel.toUpperCase()) {
            case "SMS":
                return NotificationConstants.SMS_KAFKA_TOPIC;
            case "EMAIL":
                return NotificationConstants.EMAIL_KAFKA_TOPIC;
            case "WHATSAPP":
                return NotificationConstants.WHATSAPP_KAFKA_TOPIC;
            default:
                logger.error("Invalid notification channel: {}", channel);
                return null;
        }
    }

    private boolean validate(NotificationRequest request) {
        if (request == null) {
            logger.error("Validation failed: NotificationRequest is null");
            return false;
        }

        if (request.getNotificationType() == null || request.getNotificationType().isEmpty()) {
            logger.error("Validation failed: Notification type is missing");
            return false;
        }

        if (!isValidNotificationType(request.getNotificationType())) {
            logger.error("Validation failed: Invalid notification type: {}", request.getNotificationType());
            return false;
        }

        if ((request.getPhone() == null || request.getPhone().isEmpty()) && (request.getEmail() == null || request.getEmail().isEmpty())) {
            logger.error("Validation failed: Recipient is missing");
            return false;
        }

        if (request.getNotificationContent() == null || request.getNotificationContent().isEmpty()) {
            logger.error("Validation failed: Message content is missing");
            return false;
        }

        logger.info("Validation passed for NotificationRequest: {}", request);
        return true;
    }

    private boolean isValidNotificationType(String type) {
        return "SMS".equalsIgnoreCase(type) ||
                "EMAIL".equalsIgnoreCase(type) ||
                "WHATSAPP".equalsIgnoreCase(type);
    }

    /**
     * Updates the notification status in the database.
     * If the notification with the given identifier is not found, logs a warning.
     *
     * @param identifier The unique identifier of the notification.
     * @param status     The new status to set for the notification (e.g., DELIVERED).
     */
    private void updateNotificationStatus(String identifier, String status) {
        repository.findByIdentifier(identifier).ifPresentOrElse(notification -> {
            notification.setStatus(status);
            repository.save(notification);
            logger.info("Status updated successfully for identifier: {}", identifier);
        }, () -> {
            logger.warn("Notification with identifier {} not found in the database.", identifier);
        });
    }
}
