package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.NotificationsService;
import com.geohunt.backend.database.Notifications;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notifications Controller", description = "Operations relating to notifications. Creating is managed through the websocket feature, where any notification sent gets added to the database.")
@RestController
@RequestMapping("/notifications")
public class notifController {

    @Autowired
    private NotificationsService notificationsService;

    // Get all notifications for a user
    @Operation(summary = "Get notifications for a specific user", description = "Queries database to get all notifications where target is userId. Returns NoContent if not found, or OK if found.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Notifications.class)))),
            @ApiResponse(responseCode = "204", description = "Notifications not found.", content = @Content)})
    @GetMapping
    public ResponseEntity<List<Notifications>> getNotifs(@Parameter(description = "users id", required = true) @RequestParam long userId) {
        List<Notifications> notifs = notificationsService.getMyNotifs(userId);
        if (notifs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifs);
    }

    @Operation(summary = "Delete Notification based on notification id", description = "Queries database to delete notification if the id matches the parameter. Nothing happens if notification not found.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications deleted",
                    content = @Content)})
    @DeleteMapping("/{notifId}")
    public ResponseEntity<Void> deleteNotif(@Parameter(description = "notification id", required = true) @PathVariable Long notifId) {
        notificationsService.deleteNotification(notifId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Mark notification as read", description = "Marks notification as read. Nothing happens if not found or was already read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications marked as read",
                    content = @Content)})
    @PutMapping("/{notifId}/read")
    public ResponseEntity<Void> markAsRead(@Parameter(description = "notification id", required = true) @PathVariable Long notifId) {
        notificationsService.markAsRead(notifId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Edit notification body based on notification id", description = "Edit notifications body. Also resets read to false and timestamp to when it was edited. Nothing happens if notif not found. If message is empty, notification will be empty.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification edited",
                    content = @Content)})
    @PutMapping("/editMsg")
    public ResponseEntity<Void> changeMessage(@Parameter(description = "notification id", required = true) @RequestParam long notifId, @Parameter(description = "message to change notification to", required = true) @RequestParam String message) {
        return notificationsService.editNotification(notifId, message);
    }
}

