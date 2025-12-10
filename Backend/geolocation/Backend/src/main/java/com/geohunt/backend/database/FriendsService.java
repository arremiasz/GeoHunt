package com.geohunt.backend.database;

import com.geohunt.backend.util.FriendDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.*;

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

// Reverse key
        FriendKey reverseKey = new FriendKey();
        reverseKey.setPrimaryId(targetId);
        reverseKey.setTargetId(senderId);

// Check both directions
        if (friendsRepository.existsById(key) || friendsRepository.existsById(reverseKey)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Friendship already exists.");
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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender not found.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Target not found.");
            }
        }
        Account acc = Accounts.get(0).get();
        Account target = Accounts.get(1).get();

        Optional<Friends> friendship;
        if(friendsRepository.findByPrimaryAndTarget(acc, target).isEmpty()) {
            friendship = friendsRepository.findByPrimaryAndTarget(target, acc);
        } else {
            friendship = friendsRepository.findByPrimaryAndTarget(acc, target);
        }


        if(friendship.isEmpty()) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Both accounts are not friends.");}


        friendsRepository.delete(friendship.get());
        return ResponseEntity.status(HttpStatus.OK).body("Friend removed.");
    }

    public ResponseEntity<String> acceptFriend(Long primaryId, Long targetId) {
        ArrayList<Optional<Account>> Accounts = doesAccountsExist(primaryId, targetId);
        if (Accounts.size() == 1) {
            Optional<Account> account = Accounts.get(0);
            if(account.get().getId() == targetId) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender not found.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Target not found.");
            }
        }
        Account acc;
        Account target;
        try{
            acc = Accounts.get(0).get();
            target = Accounts.get(1).get();
        } catch (IndexOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cannot find account of main or target user.");
        }


        Optional<Friends> friendship = friendsRepository.findByPrimaryAndTarget(acc, target);
        if(friendship.isEmpty()) {
            friendship = friendsRepository.findByPrimaryAndTarget(target, acc);
            if(friendship.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend Request not sent.");
            }
        }
        if(friendship.get().isAccepted()) {return ResponseEntity.status(HttpStatus.CONFLICT).body("Friend already accepted.");}
        friendship.get().setAccepted(true);
        friendsRepository.save(friendship.get());
        return ResponseEntity.status(HttpStatus.OK).body("Friend accepted.");
    }

    public ResponseEntity<List<Account>> getFriends(long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        List<Account> friends = new ArrayList<>();

        List<Friends> sentFriends = friendsRepository.findByPrimaryAndIsAcceptedTrue(account.get());
        List<Friends> receivedFriends = friendsRepository.findByTargetAndIsAcceptedTrue(account.get());


        for (Friends f : sentFriends) {
            friends.add(f.getTarget());
        }

        for (Friends f : receivedFriends) {
            friends.add(f.getPrimary());
        }

        return ResponseEntity.ok(friends);
    }

    public ResponseEntity<List<Account>> getFriendRequestsSent(long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        List<Account> friends = new ArrayList<>();
        List<Friends> f = friendsRepository.findByPrimaryAndIsAcceptedFalse(account.get());

        for (Friends f1 : f) {
            friends.add(f1.getTarget());
        }
        return ResponseEntity.ok(friends);
    }

    public ResponseEntity<List<Account>> getFriendRequestsRecieved(long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        List<Account> friends = new ArrayList<>();
        List<Friends> f = friendsRepository.findByTargetAndIsAcceptedFalse(account.get());

        for (Friends f1 : f) {
            friends.add(f1.getPrimary());
        }
        return ResponseEntity.ok(friends);
    }

    @Transactional
    public boolean deleteFriends(long id) {
        Optional<Account> accountOpt = accountRepository.findById(id);
        if (accountOpt.isEmpty()) return false;

        Account account = accountOpt.get();

        friendsRepository.deleteByPrimaryOrTarget(account, account);

        return true;
    }


    public ResponseEntity<String> rejectFriend(Long primaryId, Long targetId) {
        ArrayList<Optional<Account>> Accounts = doesAccountsExist(primaryId, targetId);
        if (Accounts.size() == 1) {
            Optional<Account> account = Accounts.get(0);
            if(account.get().getId() == targetId) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sender not found.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Target not found.");
            }
        }
        Account acc = Accounts.get(0).get();
        Account target = Accounts.get(1).get();

        Optional<Friends> friendship = friendsRepository.findByPrimaryAndTarget(acc, target);
        if(friendship.isEmpty()) {
            friendship = friendsRepository.findByPrimaryAndTarget(target, acc);
            if(friendship.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend Request does not exist.");
            }
        }
        friendsRepository.delete(friendship.get());
        return ResponseEntity.status(HttpStatus.OK).body("Friend removed.");
    }


}
