package com.geohunt.backend.Controllers;

import com.geohunt.backend.Shop.UserInventory;
import com.geohunt.backend.Shop.UserInventoryRepository;
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

import java.util.List;


@Tag(name = "Accounts Management", description = "Operations related to new and existing accounts")
@RestController
public class signupController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserInventoryRepository userInventoryRepository;

    @Operation(summary = "Create a new account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Username or email already exists",
                    content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Account object containing username, email, and password. pfp can be optional as a byte string.",
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

    @GetMapping("/account/getPfp")
    public ResponseEntity<String> getPfp(@RequestParam long id) {
        return accountService.getPfp(id);
    }

    @Operation(
            summary = "Get a user's total points",
            description = """
                Retrieves the total point balance stored on the user's account.
                Points are used for purchasing shop items such as decorations,
                cosmetics, and powerups.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user points.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "4120"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found.",
                    content = @Content
            )
    })
    @GetMapping("/points")
    public ResponseEntity getPoints(
            @Parameter(description = "User ID", required = true)
            @RequestParam long id
    ) {
        try {
            Account acc = accountService.getAccountById(id);
            return ResponseEntity.ok(acc.getTotalPoints());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @Operation(
            summary = "Add points to a user's account",
            description = """
                Adds points to a user's account. 
                Points added here can be used in the shop.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Points successfully added.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "\"Points added successfully.\""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found.",
                    content = @Content
            )
    })
    @PutMapping("/addPoints")
    public ResponseEntity addPoints(
            @Parameter(description = "User ID") @RequestParam long id,
            @Parameter(description = "Amount of points to add") @RequestParam long amount
    ) {
        try {
            Account acc = accountService.getAccountById(id);
            accountService.giveAccountMoney(acc, amount);
            return ResponseEntity.ok("Points added successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Account not found.");
        }
    }



    @Operation(
            summary = "Get a user's inventory (cosmetics, decorations, powerups)",
            description = """
                Returns all items the user owns from the shop system.  
                Includes equipped state, quantity, acquisition date, and item metadata.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved user inventory.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInventory.class),
                            examples = @ExampleObject(
                                    value = """
                                    [
                                      {
                                        "id": 12,
                                        "equipped": true,
                                        "quantity": 1,
                                        "acquiredAt": "2025-01-03T22:14:55.123+00:00",
                                        "shopItem": {
                                          "id": 4,
                                          "name": "Golden Crown",
                                          "description": "A rare cosmetic",
                                          "image": "base64string",
                                          "itemType": "DECORATION",
                                          "price": 2500
                                        }
                                      }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found.",
                    content = @Content
            )
    })
    @GetMapping("/inventory")
    public ResponseEntity getInventory(
            @Parameter(description = "User ID", required = true)
            @RequestParam long id
    ) {
        try {
            Account acc = accountService.getAccountById(id);
            List<UserInventory> inv = userInventoryRepository.findAllByUser(acc);
            return ResponseEntity.ok(inv);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Account not found.");
        }
    }
}
