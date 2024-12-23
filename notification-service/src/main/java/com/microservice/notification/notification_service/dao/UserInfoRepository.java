package com.microservice.notification.notification_service.dao;

import com.microservice.notification.notification_service.entities.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing the {@link UserInfo} entity in the database.
 * <p>
 * This interface extends JpaRepository to provide CRUD operations for {@link UserInfo} entities.
 * </p>
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    /**
     * Retrieves a {@link UserInfo} entity based on the user's name (username).
     *
     * @param username The name of the user to search for.
     * @return An {@link Optional} containing the {@link UserInfo} if found, otherwise empty.
     */
    Optional<UserInfo> findByName(String username);
}
