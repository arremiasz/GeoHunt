package com.geohunt.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogInController {

    @Autowired
    AccountRepository accountRepository;


    // Log-in (POST)
    @PostMapping(path = "/login")
    ResponseEntity<String> logIn(@RequestBody AccountLogInInfo logInRequest){
        // Verify login request
        // Create and store session ID
        // Return session ID (String?)
        return ResponseEntity.ok("login placeholder"); // temp
    }

    // Log-out (POST)
    @PostMapping(path = "/logout")
    ResponseEntity<String> logOut(@RequestBody String sessionId){
        // Verify sessionId
        // Close related session
        return ResponseEntity.ok("logout placeholder"); // temp
    }

    // Update Username (PUT)

    // Update User Info (PUT)
}
