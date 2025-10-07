package com.geohunt.backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogInController {

    @Autowired
    AccountRepository accountRepository;


    // Log-in (POST)
    @PostMapping(path = "/login")
    ResponseEntity<Long> logIn(@RequestBody Account login){

        Account account = accountRepository.findbyusername(login.getUsername());
        if(account == null){
            // account not found
            return ResponseEntity.badRequest().body(null);
        }
        if(account.getPassword().equals(login.getPassword())){
            // password is identical
            return ResponseEntity.ok(account.getId());
        }
        else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Log-out (POST)
//    @PostMapping(path = "/logout")
//    ResponseEntity<String> logOut(@RequestBody Long userId){
//
//        return ResponseEntity.ok("logout placeholder"); // temp
//    }

    // Update Username (PUT)

    // Update User Info (PUT)
}
