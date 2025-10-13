package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountService;
import com.geohunt.backend.database.FriendsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class socialController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendsService friendsService;

    @GetMapping("/friends")
    public ResponseEntity<ArrayList<Account>> getFriends(@RequestParam(required = true) long id){
        return friendsService.getFriends(id);
    }

    @PostMapping("/friends/add")
    public ResponseEntity<String> addFriend(@RequestParam long primaryId, @RequestParam long targetId) {
        return friendsService.addFriend(primaryId, targetId);
    }

    @DeleteMapping("/friends/remove")
    public ResponseEntity<String> removeFriend(@RequestParam long primaryId, @RequestParam long targetId) {
        return friendsService.removeFriend(primaryId, targetId);
    }

    @DeleteMapping("/friends/reject")
    public ResponseEntity<String> rejectFriend(@RequestParam long primaryId, @RequestParam long targetId) {
        return friendsService.rejectFriend(primaryId, targetId);
    }

    @PutMapping("/friends/accept")
    public ResponseEntity<String> acceptFriend(@RequestParam long primaryId, @RequestParam long targetId) {
        return friendsService.acceptFriend(primaryId, targetId);
    }

}

