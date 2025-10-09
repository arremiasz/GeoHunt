package com.geohunt.backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FriendsService {

    @Autowired
    FriendsRepository friendsRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ResponseEntity<String> addFriend(Long senderId, Long targetId){
        if (senderId.equals(targetId)) {
            return ResponseEntity;
        }

    }
}
