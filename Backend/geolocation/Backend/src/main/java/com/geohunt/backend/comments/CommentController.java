package com.geohunt.backend.comments;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Evan Julson
 */
@Controller
public class CommentController {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    ChallengesRepository challengesRepository;

    // Post
    @Operation(summary = "Post a comment using user id and challenge id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment post",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "400", description = "Failed to post comment", content = @Content)})
    @PostMapping("/comments")
    public ResponseEntity<Comment> postComment(@RequestParam long cid, @RequestParam long uid, @RequestBody String comment){
        try{
            if(challengesRepository.findById(cid).isEmpty()){
                return ResponseEntity.badRequest().build();
            }

            Account author = accountService.getAccountById(uid);
            Challenges challenges = challengesRepository.findById(cid).get();

            Comment newComment = new Comment(author, challenges, comment);
            commentRepository.save(newComment);
            return ResponseEntity.ok(newComment);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    // Get
    @Operation(summary = "Find a comment by comment id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Comment",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment Not Found", content = @Content)})
    @GetMapping("comments/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable long id){
        if(commentRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(commentRepository.findById(id).get());
    }

    // Put
    @Operation(summary = "Update comment message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated comment message",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Comment Not Found", content = @Content)})
    @PutMapping("/comments/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable long id, @RequestBody String newComment){
        if(commentRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Comment comment = commentRepository.findById(id).get();
        comment.setComment(newComment);
        commentRepository.save(comment);

        return ResponseEntity.ok(comment);
    }

    // Delete
    @Operation(summary = "Update comment message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Removed comment successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Comment does not exist", content = @Content)})
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable long id){
        if(commentRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Comment comment = commentRepository.findById(id).get();
        commentRepository.delete(comment);
        return ResponseEntity.ok("Removed comment");
    }

    // List
    @Operation(summary = "Lists comments by challenge id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found comment list", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content)})
    @GetMapping("/challenges/{cid}/comments")
    public ResponseEntity<Comment[]> getCommentsByChallenge(@PathVariable long cid){
        if(challengesRepository.findById(cid).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Challenges challenges = challengesRepository.findById(cid).get();
        Comment[] commentArr = challenges.getComments().toArray(new Comment[0]);
        return ResponseEntity.ok(commentArr);
    }

    @Operation(summary = "Lists comments by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found comment list", content = @Content),
            @ApiResponse(responseCode = "404", description = "Challenge not found", content = @Content)})
    @GetMapping("/account/{uid}/comments")
    public ResponseEntity<Comment[]> getCommentsByUser(@PathVariable long uid){
        try{
            Account account = accountService.getAccountById(uid);
            Comment[] commentArr = account.getComments().toArray(new Comment[0]);
            return ResponseEntity.ok(commentArr);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
