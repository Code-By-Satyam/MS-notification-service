package com.microservice.notification.notification_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user in the system, containing essential details such as username, password, and role.
 * <p>
 * This entity is mapped to the "USERINFO" table in the database and is used for authentication and authorization
 * purposes.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "USERINFO")
public class UserInfo {

    /**
     * The unique identifier for the user. This value is auto-generated and serves as the primary key in the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userid;

    /**
     * The username of the user.
     * <p>
     * This value is used for logging in and for identifying the user within the system.
     * </p>
     */
    private String name;

    /**
     * The password of the user, typically stored in an encrypted format.
     * <p>
     * This is used for verifying the user's identity during the authentication process.
     * </p>
     */
    private String password;

    /**
     * The role(s) assigned to the user, defining their level of access within the system.
     * <p>
     * Roles could be something like "USER", "ADMIN", or any custom roles that define the user's permissions.
     * </p>
     */
    private String role;
}
