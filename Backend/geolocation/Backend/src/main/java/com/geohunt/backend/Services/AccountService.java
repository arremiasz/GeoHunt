package com.geohunt.backend.Services;

import com.geohunt.backend.Shop.ShopRepository;
import com.geohunt.backend.Shop.TransactionsRepository;
import com.geohunt.backend.Shop.UserInventoryRepository;
import com.geohunt.backend.database.*;
import com.geohunt.backend.powerup.Powerup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FriendsService friendsService;

    @Autowired
    private ChallengesRepository challengeService;

    @Autowired
    private NotificationsRepository notificationsService;

    @Autowired
    private SubmissionsRepository submissionsRepository;

    @Autowired
    private UserInventoryRepository userInventoryRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    public long getIdByUsername(String username) {
        Account a = getAccountByUsername(username);
        return a.getId();
    }

    public long createAccount(Account account) {
        if(accountRepository.findByUsername(account.getUsername()).isPresent()){
            return -1;
        } else if(accountRepository.findByEmail(account.getEmail()).isPresent()) {
           return -2;
        }
        accountRepository.save(account);
        return account.getId();
    }

    public Account getAccountByUsername(String username) {
        if(accountRepository.findByUsername(username).isPresent()) {
            return accountRepository.findByUsername(username).get();
        }
        throw new IllegalArgumentException("Username Incorrect");
    }

    public Account getAccountById(Long id) {
        if(accountRepository.findById(id).isPresent()) {
            return accountRepository.findById(id).get();
        }
        throw new IllegalArgumentException("Account does not exist.");
    }

    public boolean deleteFriends(long id) {
        return friendsService.deleteFriends(id);
    }

    public void deleteChallenges(long id) {
        challengeService.deleteByCreator_Id(id);
    }

    public void deleteNotifications(long id) {
        notificationsService.deleteAllByTargetId(id);
    }

    public void deleteSubmissions(long id) {
        submissionsRepository.deleteById(id);
    }

    public void deletePowerups(long id){
        Optional<Account> accOpt = accountRepository.findById(id);

        Account acc = accOpt.get();

        for(Powerup p : acc.getPowerups()) {
            p.getAccounts().remove(acc);
        }

        acc.getPowerups().clear();
        
    }

    public void deleteUserInventory(long id) {
        Optional<Account> accOpt = accountRepository.findById(id);
        Account acc = accOpt.get();
        userInventoryRepository.deleteAllByUser(acc);
    }

    public void deleteTransactions(long id) {
        Optional<Account> accOpt = accountRepository.findById(id);
        Account acc = accOpt.get();
        transactionsRepository.deleteAllByUser(acc);
    }

    public boolean deleteAccountByID(Long id) {
        if(accountRepository.findById(id).isPresent()) {
            deleteFriends(id);
            deleteSubmissions(id);
            deleteChallenges(id);
            deleteNotifications(id);
            deletePowerups(id);
            deleteUserInventory(id);
            deleteTransactions(id);
            accountRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteAccountByUsername(String username) {
        if(accountRepository.findByUsername(username).isPresent()) {
            accountRepository.deleteById(accountRepository.findByUsername(username).get().getId());
            return true;
        } else {
            return false;
        }
    }

    public ResponseEntity<String> updatedAccount(Long id, Account account) {
        Account acc = getAccountById(id);
        if (acc == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account not found with id: " + id);
        }


        if (account.getUsername() != null && !account.getUsername().isBlank()) {
            Optional<Account> existingUsername = accountRepository.findByUsername(account.getUsername());
            if (existingUsername.isPresent() && existingUsername.get().getId() != id) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username already taken: " + account.getUsername());
            } else {
                acc.setUsername(account.getUsername());
            }
        }


        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            acc.setPassword(account.getPassword());
        }


        if (account.getEmail() != null && !account.getEmail().isEmpty()) {
            Optional<Account> existingEmail = accountRepository.findByEmail(account.getEmail());
            if (existingEmail.isPresent() && existingEmail.get().getId() != id) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use: " + account.getEmail());
            } else {
                acc.setEmail(account.getEmail());
            }
        }


        if (account.getPfp() != null && !account.getPfp().isEmpty()) {
            acc.setPfp(account.getPfp());
        }

        accountRepository.save(acc);

        return ResponseEntity.ok("Account updated successfully!");
    }

    public ResponseEntity<String> getPfp(long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isPresent()) {
            return ResponseEntity.ok(accountOptional.get().getPfp());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
