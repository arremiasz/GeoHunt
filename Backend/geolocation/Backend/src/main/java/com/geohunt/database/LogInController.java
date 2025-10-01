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
    ResponseEntity<Account> logIn(@RequestBody Account logInRequest){
        try{
            Account account = accountRepository.findbyusername(logInRequest.getUsername());
            if(logInRequest.password.equals(account.password)){
                //Needs to implement security?
                return ResponseEntity.ok(account);
            }
            else{
                return ResponseEntity.badRequest().build();
            }
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(404).build();
        }

    }

    // Update Username (PUT)

    // Update User Info (PUT)
}
