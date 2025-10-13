package com.geohunt.backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendsService {

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private AccountRepository accountRepository;

    public ArrayList<Optional<Account>> doesAccountsExist(long primaryId, long targetId) {
        Optional<Account> account = accountRepository.findById(primaryId);
        Optional<Account> target = accountRepository.findById(targetId);
        ArrayList<Optional<Account>> returnable = new ArrayList<>();

        if (account.isPresent() && target.isPresent()) {

            returnable.add(account);
            returnable.add(target);
            return returnable;
        } else if(account.isPresent() && !target.isPresent()) {
            returnable.add(account);
        } else if(target.isPresent() && !account.isPresent()) {
            returnable.add(target);
        }
        return new ArrayList<>();
    }

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

    public ResponseEntity<String> removeFriend(Long accountId, Long targetId) {
        ArrayList<Optional<Account>> Accounts = doesAccountsExist(accountId, targetId);
        if (Accounts.size() == 1) {
            Optional<Account> account = Accounts.get(0);
            if(account.get().getId() == targetId) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find account of main user.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot find account of target.");
            }
        }
        Account acc = Accounts.get(0).get();
        Account target = Accounts.get(1).get();

        Optional<Friends> friendship = friendsRepository.findByPrimaryAndTarget(acc, target);
        if(friendship.isEmpty()) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend not found.");}


        friendsRepository.delete(friendship.get());
        return ResponseEntity.status(HttpStatus.OK).body("Friend removed.");
    }

    public ResponseEntity<String> acceptFriend(Long primaryId, Long targetId) {
        ArrayList<Optional<Account>> Accounts = doesAccountsExist(primaryId, targetId);
        if (Accounts.size() == 1) {
            Optional<Account> account = Accounts.get(0);
            if(account.get().getId() == targetId) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find account of main user.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot find account of target.");
            }
        }
        Account acc = Accounts.get(0).get();
        Account target = Accounts.get(1).get();

        Optional<Friends> friendship = friendsRepository.findByPrimaryAndTarget(acc, target);
        if(friendship.isEmpty()) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend Request not sent.");}
        if(friendship.get().isAccepted()) {return ResponseEntity.status(HttpStatus.CONFLICT).body("Friend already accepted.");}
        friendship.get().setAccepted(true);
        friendsRepository.save(friendship.get());
        return ResponseEntity.status(HttpStatus.OK).body("Friend accepted.");
    }

    public ResponseEntity<ArrayList<Account>> getFriends(long id){
        Optional<Account> account = accountRepository.findById(id);
        if(account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        }
        List<Friends> friends = friendsRepository.findByPrimaryAndIsAcceptedTrue(account.get());
        ArrayList<Account> accounts = new ArrayList<>();
        for(Friends friend : friends) {
            accounts.add(friend.getTarget());
        }
        return ResponseEntity.status(HttpStatus.OK).body(accounts);
    }

    public ResponseEntity<String> rejectFriend(Long primaryId, Long targetId) {
        ArrayList<Optional<Account>> Accounts = doesAccountsExist(primaryId, targetId);
        if (Accounts.size() == 1) {
            Optional<Account> account = Accounts.get(0);
            if(account.get().getId() == targetId) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find account of main user.");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot find account of target.");
            }
        }
        Account acc = Accounts.get(0).get();
        Account target = Accounts.get(1).get();

        Optional<Friends> friendship = friendsRepository.findByPrimaryAndTarget(acc, target);
        if(friendship.isEmpty()) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend Request does not exist.");}
        friendsRepository.delete(friendship.get());
        return ResponseEntity.status(HttpStatus.OK).body("Friend removed.");
    }
}

