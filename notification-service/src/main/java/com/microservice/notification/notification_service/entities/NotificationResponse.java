package com.microservice.notification.notification_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the response for a notification request, containing the unique identifier and status of the notification.
 * <p>
 * This class is used to provide feedback on the status of a notification request, such as whether it was successfully
 * processed or if there was an error.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    /**
     * The unique identifier for the notification.
     */
    private String identifier;

    /**
     * The current status of the notification (e.g., PENDING, DELIVERED).
     */
    private String status;

    /**
     * Creates a NotificationResponse from a given NotificationRequest.
     *
     * @param notificationRequest The notification request object to extract information from.
     * @return A NotificationResponse object containing the identifier and status from the request.
     */
    public static NotificationResponse getResponse(NotificationRequest notificationRequest) {
        NotificationResponse response = new NotificationResponse();
        response.setIdentifier(notificationRequest.getIdentifier());
        response.setStatus(notificationRequest.getStatus());
        return response;
    }
}
