package com.geohunt.backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public void createAccount(Account account) {
        if(accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Account already exists. Username found in database.");
        }
        accountRepository.save(account);
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

    public boolean updatedAccount(Long id, Account account) {
        try{
            Account acc = getAccountById(id);
            if(!account.getUsername().isBlank()) {
                acc.setUsername(account.getUsername());
            }
            if(!account.getPassword().isEmpty()) {
                acc.setPassword(account.getPassword());
            }
            if(!(account.getEmail().isEmpty()) && !(accountRepository.findByEmail(account.getEmail()).isPresent())) {
                acc.setEmail(account.getEmail());
            }
            if(!account.getPfp().isEmpty()) {
                acc.setPfp(account.getPfp());
            }
            accountRepository.save(acc);
            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Account does not exist.");
        }

    }

}
