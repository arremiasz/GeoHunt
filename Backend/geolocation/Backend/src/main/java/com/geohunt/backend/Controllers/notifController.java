package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.NotificationsService;
import com.geohunt.backend.database.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class notifController {

    @Autowired
    private NotificationsService notificationsService;

    // Get all notifications for a user
    @GetMapping
    public ResponseEntity<List<Notifications>> getNotifs(@RequestParam long userId) {
        List<Notifications> notifs = notificationsService.getMyNotifs(userId);
        if (notifs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifs);
    }

    // Delete a notification by ID
    @DeleteMapping("/{notifId}")
    public ResponseEntity<Void> deleteNotif(@PathVariable Long notifId) {
        notificationsService.deleteNotification(notifId);
        return ResponseEntity.ok().build();
    }

    // (Optional) Mark as read
    @PutMapping("/{notifId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notifId) {
        notificationsService.markAsRead(notifId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/editMsg")
    public ResponseEntity<Void> changeMessage(@RequestParam long notifId, @RequestParam String message) {
        return notificationsService.editNotification(notifId, message);
    }
}

