package com.geohunt.backend.notifications;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Long> {
    List<Notifications> findAllByTargetId(Long targetId);
    @Transactional
    void deleteAllByTargetId(Long targetId);
}

