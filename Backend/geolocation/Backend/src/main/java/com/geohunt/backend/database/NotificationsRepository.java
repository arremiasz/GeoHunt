package com.geohunt.backend.database;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
    Optional<Notifications> findByTargetId(Long targetId);
}
