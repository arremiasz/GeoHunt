package com.geohunt.backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public void createAccount(Account account) {
        if(accountRepository.findByUsername(account.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Account already exists");
        }
        accountRepository.save(account);
    }

    public Account getAccountByUsername(String username) {
        if(accountRepository.findByUsername(username).isPresent()) {
            return accountRepository.findByUsername(username).get();
        }
        throw new IllegalArgumentException("Account does not exist.");
    }
}
