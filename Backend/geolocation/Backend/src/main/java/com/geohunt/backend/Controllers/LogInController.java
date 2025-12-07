package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.LogInInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login Controller", description = "Login endpoint")
@RestController
public class LogInController {

    @Autowired
    AccountService accountService;


    // Log-in (POST)
    // Takes JSON in a request body with the format {"username":"username","password":"password"}
    // Returns a Long for userId.
    @Operation(summary = "user login using username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))
            }),
            @ApiResponse(responseCode = "400", description = "Login failed", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))
            })
    })
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
}
