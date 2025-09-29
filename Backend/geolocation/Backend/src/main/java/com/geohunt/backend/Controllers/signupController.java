package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class signupController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody Account account) {
        try {
            accountService.createAccount(account);
            return ResponseEntity.ok("Account created");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Username Already Exists");
        }
    }
}

