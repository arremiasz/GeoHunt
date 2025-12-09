package com.geohunt.backend.Controllers;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.FriendsService;
import com.geohunt.backend.util.FriendDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Friends Controller", description = "Operations relating to friends")
@RestController
public class socialController {

    @Autowired
    private FriendsService friendsService;

    @Operation(summary = "Get the list of accepted friends for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friends retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Account.class)))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/friends")
    public ResponseEntity<List<Account>> getFriends(
            @Parameter(description = "User ID") @RequestParam(required = true) long id) {
        return friendsService.getFriends(id);
    }

    @Operation(summary = "Get friend requests the user has received")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend requests retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Account.class)))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/friendRequestsRecieved")
    public ResponseEntity<List<Account>> getFriendRequestsRecieved(
            @Parameter(description = "User ID") @RequestParam(required = true) long id) {
        return friendsService.getFriendRequestsRecieved(id);
    }

    @Operation(summary = "Get friend requests the user has sent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sent friend requests retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Account.class)))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/friendRequestsSent")
    public ResponseEntity<List<Account>> getFriendRequestsSent(
            @Parameter(description = "User ID") @RequestParam(required = true) long id) {
        return friendsService.getFriendRequestsSent(id);
    }

    @Operation(summary = "Send a friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Friend request sent successfully", content = @Content),
            @ApiResponse(responseCode = "409", description = "Invalid input or request already exists", content = @Content),
            @ApiResponse(responseCode = "404", description = "One or both users not found", content = @Content)
    })
    @PostMapping("/friends/add")
    public ResponseEntity<String> addFriend(
            @Parameter(description = "ID of user sending the request") @RequestParam long primaryId,
            @Parameter(description = "ID of user receiving the request") @RequestParam long targetId) {
        return friendsService.addFriend(primaryId, targetId);
    }

    @Operation(summary = "Remove an existing friend")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend removed successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Friendship not found, or one or both users not found", content = @Content)
    })
    @DeleteMapping("/friends/remove")
    public ResponseEntity<String> removeFriend(
            @Parameter(description = "Primary user ID") @RequestParam long primaryId,
            @Parameter(description = "Friend user ID to remove") @RequestParam long targetId) {
        return friendsService.removeFriend(primaryId, targetId);
    }

    @Operation(summary = "Reject a pending friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request rejected successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Friend request not found, or one or both users not found", content = @Content)
    })
    @DeleteMapping("/friends/reject")
    public ResponseEntity<String> rejectFriend(
            @Parameter(description = "Primary user ID") @RequestParam long primaryId,
            @Parameter(description = "User ID whose request is being rejected") @RequestParam long targetId) {
        return friendsService.rejectFriend(primaryId, targetId);
    }

    @Operation(summary = "Accept a pending friend request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request accepted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Friend request not found, or one or both users not found", content = @Content)
    })
    @PutMapping("/friends/accept")
    public ResponseEntity<String> acceptFriend(
            @Parameter(description = "Primary user ID") @RequestParam long primaryId,
            @Parameter(description = "User ID whose request is being accepted") @RequestParam long targetId) {
        return friendsService.acceptFriend(primaryId, targetId);
    }

}
