package com.geohunt.backend.Controllers;

import com.geohunt.backend.Services.NotificationsService;
import com.geohunt.backend.database.Notifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class notifController {

    @Autowired
    private NotificationsService notificationsService;

    @GetMapping("/notifications/name")
    public ResponseEntity<List<Notifications>> getNotifs(@RequestParam long id) {

        List<Notifications> notifs = notificationsService.getMyNotifs(id);
        if (notifs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(notifs, HttpStatus.OK);

    }
}
