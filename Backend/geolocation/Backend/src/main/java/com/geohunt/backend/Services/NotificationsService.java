package com.geohunt.backend.Services;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountRepository;
import com.geohunt.backend.database.Notifications;
import com.geohunt.backend.database.NotificationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationsService {

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Create and store a new notification by username
    public void sendNotificationToUser(String username, String message) {
        Optional<Account> target = accountRepository.findByUsername(username);
        if (target != null) {
            Notifications notif = new Notifications();
            notif.setMessage(message);
            notif.setTarget(target.get());
            notificationsRepository.save(notif);
        }
    }

    // Get all notifications for a user ID
    public List<Notifications> getMyNotifs(Long targetId) {
        return notificationsRepository.findAllByTargetId(targetId);
    }

    // Delete a single notification by its ID
    public void deleteNotification(Long notifId) {
        notificationsRepository.deleteById(notifId);
    }

    // Optionally mark notification as read
    public void markAsRead(Long notifId) {
        notificationsRepository.findById(notifId).ifPresent(notif -> {
            notif.setReadStatus(true);
            notificationsRepository.save(notif);
        });
    }

    public void saveNotification(Account target, String message) {
        Notifications n = new Notifications();
        n.setTarget(target);
        n.setMessage(message);
        n.setReadStatus(false);
        n.setSentAt(LocalDateTime.now());
        notificationsRepository.save(n);
    }

    public ResponseEntity<Void> editNotification(long notifId, String message) {
        notificationsRepository.findById(notifId).ifPresent(notif -> {
            notif.setMessage(message);
            notif.setReadStatus(false);
            notif.setSentAt(LocalDateTime.now());
            notificationsRepository.save(notif);
        }
        );
        return ResponseEntity.ok().build();
    }

}
