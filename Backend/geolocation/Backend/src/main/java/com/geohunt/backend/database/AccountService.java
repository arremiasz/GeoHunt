package com.geohunt.backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

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

    public boolean deleteAccountByID(Long id) {
        if(accountRepository.findById(id).isPresent()) {
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

        // ✅ Username
        if (account.getUsername() != null && !account.getUsername().isBlank()) {
            Optional<Account> existingUsername = accountRepository.findByUsername(account.getUsername());
            if (existingUsername.isPresent() && existingUsername.get().getId() != id) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Username already taken: " + account.getUsername());
            } else {
                acc.setUsername(account.getUsername());
            }
        }

        // ✅ Password
        if (account.getPassword() != null && !account.getPassword().isEmpty()) {
            acc.setPassword(account.getPassword());
        }

        // ✅ Email
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

}
