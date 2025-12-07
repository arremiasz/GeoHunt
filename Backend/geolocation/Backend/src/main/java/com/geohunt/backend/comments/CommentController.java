package com.geohunt.backend.comments;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountRepository;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CommentController {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    ChallengesRepository challengesRepository;

    // Post
    @PostMapping("challenges/{cid}/comments")
    public ResponseEntity<Comment> postComment(@PathVariable long cid, @RequestParam long uid, @RequestBody String comment){
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
    @GetMapping("comments/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable long id){
        if(commentRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(commentRepository.findById(id).get());
    }

    // Put
    @PutMapping("/comments/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable long id, @RequestBody String newComment){
        if(commentRepository.findById(id).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Comment comment = commentRepository.findById(id).get();
        comment.setText(newComment);
        commentRepository.save(comment);

        return ResponseEntity.ok(comment);
    }

    // Delete
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable long id){
        if(commentRepository.findById(id).isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        Comment comment = commentRepository.findById(id).get();
        commentRepository.delete(comment);
        return ResponseEntity.ok("Removed comment");
    }

    // List
    @GetMapping("/challenges/{cid}/comments")
    public ResponseEntity<Comment[]> getCommentsByChallenge(@PathVariable long cid){
        if(challengesRepository.findById(cid).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Challenges challenges = challengesRepository.findById(cid).get();
        Comment[] commentArr = challenges.getComments().toArray(new Comment[0]);
        return ResponseEntity.ok(commentArr);
    }

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
