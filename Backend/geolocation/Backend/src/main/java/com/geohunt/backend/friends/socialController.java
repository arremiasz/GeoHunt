package com.geohunt.backend.friends;

import com.geohunt.backend.account.Account;
import com.geohunt.backend.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/friendRequestsRecieved")
    public ResponseEntity<List<Account>> getFriendRequestsRecieved(@RequestParam(required = true) long id){
        return friendsService.getFriendRequestsRecieved(id);
    }

    @GetMapping("/friendRequestsSent")
    public ResponseEntity<List<Account>> getFriendRequestsSent(@RequestParam(required = true) long id){
        return friendsService.getFriendRequestsSent(id);
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
