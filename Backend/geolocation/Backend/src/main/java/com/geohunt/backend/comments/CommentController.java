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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CommentController {
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    ChallengesRepository challengesRepository;

    // Post
    @PostMapping("challenge/{id}/comments")
    public ResponseEntity<Comment> postComment(@PathVariable long challengeId, @RequestParam long accountId, @RequestBody String comment){
        try{
            Account author = accountService.getAccountById(accountId);
            Challenges challenges = challengesRepository.findById(challengeId).get();

            Comment newComment = new Comment(author, challenges, comment);
            commentRepository.save(newComment);
            return ResponseEntity.ok(newComment);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().build();
        }
    }

    // Get

    // Put

    // Delete

    // List
}
