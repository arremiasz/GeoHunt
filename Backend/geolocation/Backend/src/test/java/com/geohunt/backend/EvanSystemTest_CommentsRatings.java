package com.geohunt.backend;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.comments.CommentRepository;
import com.geohunt.backend.database.AccountRepository;
import com.geohunt.backend.database.ChallengesRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EvanSystemTest_CommentsRatings {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ChallengesRepository challengesRepository;

    @BeforeAll
    public void setup(@Autowired CommentRepository commentRepository, @Autowired AccountService accountService, @Autowired ChallengesRepository challengesRepository){

    }

    @Test
    @Order(1)
    public void comments_postComment(){

    }

    @Test
    @Order(2)
    public void comments_getComment(){

    }

    @Test
    @Order(3)
    public void comments_updateComment(){

    }

    @Test
    @Order(4)
    public void comments_deleteComment(){

    }

    @Test
    @Order(5)
    public void comments_listCommentsByChallenge(){

    }

    @Test
    @Order(6)
    public void comments_listCommentsByUser(){

    }
}
