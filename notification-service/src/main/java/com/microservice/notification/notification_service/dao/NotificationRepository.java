package com.microservice.notification.notification_service.dao;

import com.microservice.notification.notification_service.entities.NotificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for accessing the {@link NotificationRequest} entity in the database.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations for NotificationRequest entities.
 * </p>
 */
@Repository
public interface NotificationRepository extends JpaRepository<NotificationRequest, Integer> {

    /**
     * Retrieves a NotificationRequest entity based on its unique identifier.
     *
     * @param identifier The unique identifier of the notification.
     * @return An {@link Optional} containing the NotificationRequest if found, otherwise empty.
     */
    Optional<NotificationRequest> findByIdentifier(String identifier);
}
