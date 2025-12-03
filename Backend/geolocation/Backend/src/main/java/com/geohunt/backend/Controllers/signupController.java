package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.Services.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Tag(name = "Accounts Management", description = "Operations related to new and existing accounts")
@RestController
public class signupController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Create a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Username or email already exists",
                    content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Account object containing username, email, and password",
            required = true,
            content = @Content(schema = @Schema(implementation = Account.class)))
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

    @Operation(summary = "Get an account by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @GetMapping("/account/byName")
    public ResponseEntity<Account> getAccount(
            @Parameter(description = "Account username") @RequestParam String name) {
        try{
            Account acc = accountService.getAccountByUsername(name);
            return ResponseEntity.ok(acc);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get an account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @GetMapping("/account/byId")
    public ResponseEntity<Account> getAccount(
            @Parameter(description = "Account ID") @RequestParam Long id) {
        try{
            Account acc = accountService.getAccountById(id);
            return ResponseEntity.ok(acc);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete an account by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @DeleteMapping("/account/byName")
    public ResponseEntity<String> deleteAccount(
            @Parameter(description = "Account username") @RequestParam String name) {
        boolean resp = accountService.deleteAccountByUsername(name);
        if(resp){
            return ResponseEntity.ok("Account deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete an account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @DeleteMapping("/account/byId")
    public ResponseEntity<String> deleteAccount(
            @Parameter(description = "Account ID") @RequestParam long id) {
        boolean resp = accountService.deleteAccountByID(id);
        if(resp){
            return ResponseEntity.ok("Account deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update an existing account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated account object. All fields must exist in accounts class, fields left blank will not be changed. Fields with any value will.",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = Account.class),
                    examples = @ExampleObject(
                            name = "Change Name",
                            description = "Changing just username and nothing else. Other fields must be present.",
                            value = "{\"username\": \"newName\", \"pfp\": \"\", \"email\": \"\", \"password\" : \"\"}"
                    )

            ))
    @PutMapping("/account/update")
    public ResponseEntity<String> updateName(
            @Parameter(description = "Account ID") @RequestParam Long id,
            @RequestBody Account account) {
        try{
            return accountService.updatedAccount(id, account);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
