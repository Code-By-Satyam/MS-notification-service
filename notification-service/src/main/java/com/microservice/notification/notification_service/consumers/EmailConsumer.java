package com.microservice.notification.notification_service.consumers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.microservice.notification.notification_service.constants.NotificationConstants;
import com.microservice.notification.notification_service.constants.NotificationStatusEnum;
import com.microservice.notification.notification_service.dao.NotificationRepository;
import com.microservice.notification.notification_service.entities.NotificationRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Consumer service for processing email notifications from Kafka.
 * <p>
 * This service listens to the EMAIL Kafka topic, validates the incoming messages,
 * processes email delivery using a third-party API, and updates the notification status in the database.
 * </p>
 */
@Service
public class EmailConsumer {

    private static final Logger logger = LogManager.getLogger(EmailConsumer.class);
    private final Gson gson = new Gson();

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Kafka listener for processing messages from the EMAIL Kafka topic.
     *
     * @param message The raw Kafka message in JSON format.
     */
    @KafkaListener(topics = NotificationConstants.EMAIL_KAFKA_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        logger.info("Received message from EMAIL Kafka topic: {}", message);
        try {
            // Deserialize the JSON message to a NotificationRequest object
            NotificationRequest notificationRequest = gson.fromJson(message, NotificationRequest.class);

            // Validate the message content
            if (validate(notificationRequest)) {
                // Deliver email using third-party API
                sendEmail(notificationRequest);
                // Update notification status to "DELIVERED"
                updateNotificationStatus(notificationRequest.getIdentifier(), NotificationStatusEnum.DELEIVERED.name());
                logger.info("Email delivered and status updated for identifier: {}", notificationRequest.getIdentifier());
            } else {
                logger.warn("Validation failed for message: {}", message);
            }
        } catch (JsonSyntaxException e) {
            logger.error("Invalid message format received: {}", message, e);
        } catch (Exception e) {
            logger.error("Unexpected error while processing EMAIL message: {}", message, e);
        }
    }

    /**
     * Sends an email using a third-party email delivery service.
     *
     * @param notificationRequest The notification details containing the recipient, subject, and message body.
     */
    private void sendEmail(NotificationRequest notificationRequest) {
        logger.info("Sending email to recipient: {}", notificationRequest.getEmail());
        String emailApiUrl = "https://api.maildelivery.com/send";
        String payload = gson.toJson(notificationRequest);

        try {
            restTemplate.postForEntity(emailApiUrl, payload, String.class);
            logger.info("Email sent successfully to: {}", notificationRequest.getEmail());
        } catch (Exception ex) {
            logger.error("Error while sending email to: {}", notificationRequest.getEmail(), ex);
        }
    }

    /**
     * Validates the NotificationRequest object to ensure all required fields are present.
     *
     * @param notificationRequest The deserialized NotificationRequest object.
     * @return true if the request is valid; false otherwise.
     */
    private boolean validate(NotificationRequest notificationRequest) {
        if (notificationRequest == null) {
            logger.error("Validation failed: NotificationRequest is null");
            return false;
        }

        if (notificationRequest.getIdentifier() == null || notificationRequest.getIdentifier().isEmpty()) {
            logger.error("Validation failed: Identifier is missing");
            return false;
        }

        if (notificationRequest.getEmail() == null || notificationRequest.getEmail().isEmpty()) {
            logger.error("Validation failed: Recipient email is missing");
            return false;
        }

        if (notificationRequest.getNotificationContent() == null || notificationRequest.getNotificationContent().isEmpty()) {
            logger.error("Validation failed: Email message content is missing");
            return false;
        }

        return true;
    }

    /**
     * Updates the notification status in the database.
     * If the notification with the given identifier is not found, logs a warning.
     *
     * @param identifier The unique identifier of the notification.
     * @param status     The new status to set for the notification (e.g., DELIVERED).
     */
    private void updateNotificationStatus(String identifier, String status) {
        notificationRepository.findByIdentifier(identifier).ifPresentOrElse(notification -> {
            notification.setStatus(status);
            notificationRepository.save(notification);
            logger.info("Status updated successfully for identifier: {}", identifier);
        }, () -> {
            logger.warn("Notification with identifier {} not found in the database.", identifier);
        });
    }
}
