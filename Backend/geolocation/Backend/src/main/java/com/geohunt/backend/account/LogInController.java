package com.geohunt.backend.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogInController {

    @Autowired
    AccountService accountService;


    // Log-in (POST)
    // Takes JSON in a request body with the format {"username":"username","password":"password"}
    // Returns a Long for userId.
    @PostMapping(path = "/login")
    ResponseEntity<Long> logIn(@RequestBody LogInInfo login){

        try{
            Account account = accountService.getAccountByUsername(login.getUsername());
            if(account.getPassword().equals(login.getPassword())){
                // password is identical
                return ResponseEntity.ok(account.getId());
            }
            else {
                return ResponseEntity.badRequest().body(null);
            }
        }
        catch (IllegalArgumentException e){
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
