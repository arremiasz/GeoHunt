package com.geohunt.database;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Session contains information about a currently logged-in user/client.
 */
@Entity
@Getter
@Setter
public class Session {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id; //
    private Account account;
    private LocalDateTime creationTime;
    private LocalDateTime lastUse;
}
