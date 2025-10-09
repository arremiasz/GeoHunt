package com.geohunt.backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FriendsService {

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ResponseEntity<String> addFriend(Long senderId, Long targetId){
        if (senderId.equals(targetId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("You cannot friend  yourself.");
        }

        FriendKey key = new FriendKey();
        key.setPrimaryId(senderId);
        key.setTargetId(targetId);

        if (friendsRepository.existsById(key)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Friend already exists.");
        }
        Optional<Account> acc = accountRepository.findById(senderId);
        Optional<Account> target = accountRepository.findById(targetId);

        if(acc.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender not found.");
        } else if(target.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Target not found.");
        }


        Friends friendship = new Friends();
        friendship.setId(key);
        friendship.setPrimary(acc.get());
        friendship.setTarget(target.get());
        friendship.setAccepted(false);

        friendsRepository.save(friendship);
        return ResponseEntity.status(HttpStatus.CREATED).body("Friend added.");
    }
}

