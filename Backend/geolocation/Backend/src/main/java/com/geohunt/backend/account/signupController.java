package com.geohunt.backend.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class signupController {
    @Autowired
    private AccountService accountService;

    @PostMapping(value = "/signup")
    public ResponseEntity<String> signup(@RequestBody Account account) {
        try {
            long id = accountService.createAccount(account);
            if(id == -1){
                return ResponseEntity.badRequest().body("username exists");
            } else if (id == -2){
                return ResponseEntity.badRequest().body("email exists");
            }
            return ResponseEntity.ok(String.format("{\"id\":%d}", id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("username exists");
        }
    }

    @GetMapping("/account/byName")
    public ResponseEntity<Account> getAccount(@RequestParam String name) {
        try{
            Account acc = accountService.getAccountByUsername(name);
            return ResponseEntity.ok(acc);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/account/byId")
    public ResponseEntity<Account> getAccount(@RequestParam Long id) {
        try{
            Account acc = accountService.getAccountById(id);
            return ResponseEntity.ok(acc);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/account/byName")
    public ResponseEntity<String> deleteAccount(@RequestParam String name) {
        boolean resp = accountService.deleteAccountByUsername(name);
        if(resp){
            return ResponseEntity.ok("Account deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/account/byId")
    public ResponseEntity<String> deleteAccount(@RequestParam long id) {
        boolean resp = accountService.deleteAccountByID(id);
        if(resp){
            return ResponseEntity.ok("Account deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/account/update")
    public ResponseEntity<String> updateName(@RequestParam Long id, @RequestBody Account account) {
        try{
            return accountService.updatedAccount(id, account);

        } catch(IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

