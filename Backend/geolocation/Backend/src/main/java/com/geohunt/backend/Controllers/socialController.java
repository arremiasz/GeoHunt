package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.FriendsService;
import com.geohunt.backend.util.FriendDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class socialController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private FriendsService friendsService;

    @GetMapping("/friends")
    public ResponseEntity<List<Account>> getFriends(@RequestParam(required = true) long id){
        return friendsService.getFriends(id);
    }

//    @GetMapping("/friendRequestsRecieved")
//    public ResponseEntity<Account> getFriendRequestsRecieved(@RequestParam(required = true) long id){
//        return friendsService.getFriendRequestsRecieved(id);
//    }
//
//    @GetMapping("/friendRequestsSent")
//    public ResponseEntity<Account> getFriendRequestsSent(@RequestParam(required = true) long id){
//        return friendsService.getFriendRequestsSent(id);
//    }

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
