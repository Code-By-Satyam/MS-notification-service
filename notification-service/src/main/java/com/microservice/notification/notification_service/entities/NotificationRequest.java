package com.microservice.notification.notification_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a notification request that contains details about the notification to be sent.
 * <p>
 * This entity is mapped to the "NOTIFICATION" table in the database and holds information
 * such as the recipient's contact details, notification type, content, and its status.
 * </p>
 */
@Entity
@Table(name = "NOTIFICATION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    /**
     * The unique identifier of the notification record in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /**
     * The phone number of the recipient (if the notification is sent via SMS).
     */
    private String phone;

    /**
     * The email address of the recipient (if the notification is sent via email).
     */
    private String email;

    /**
     * The unique identifier for the notification.
     */
    private String identifier;

    /**
     * The type of notification (e.g., SMS, Email, etc.).
     */
    private String notificationType;

    /**
     * The priority level of the notification (e.g., HIGH, LOW).
     */
    private String priority;

    /**
     * The content/message of the notification.
     */
    private String notificationContent;

    /**
     * The current status of the notification (e.g., PENDING, DELIVERED).
     */
    private String status;
}
