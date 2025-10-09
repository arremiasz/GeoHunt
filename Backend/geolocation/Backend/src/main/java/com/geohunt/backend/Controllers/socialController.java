package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.AccountService;
import com.geohunt.backend.database.FriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class socialController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendsService friendsService;

    @PutMapping("/friends/add")
    public ResponseEntity<String> addFriend(@RequestParam long primaryId, @RequestParam long targetId) {
        return friendsService.addFriend(primaryId, targetId);
    }

}

