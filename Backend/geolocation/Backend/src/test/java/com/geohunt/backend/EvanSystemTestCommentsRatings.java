package com.geohunt.backend;

import com.geohunt.backend.comments.CommentRepository;
import com.geohunt.backend.database.*;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.response.Response;


import java.time.LocalDate;
import java.util.*;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EvanSystemTestCommentsRatings {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ChallengesRepository challengesRepository;

    ArrayList<Long> commentIdList;

    ArrayList<Long> accountIdList;

    ArrayList<Long> challengesIdList;

    @LocalServerPort
    int port;

    @BeforeAll
    public void setup(){
        // RestAssured setup
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // Test db setup
        commentIdList = new ArrayList<>();
        accountIdList = new ArrayList<>();
        challengesIdList = new ArrayList<>();

        // Dummy Accounts
        Account account1 = new Account();
        account1.setUsername("test1");
        account1.setPassword("abc");
        account1.setEmail("test@email.com");
        accountRepository.save(account1);
        accountIdList.add(account1.getId());

        Account account2 = new Account();
        account2.setUsername("test2");
        account2.setPassword("abc");
        account2.setEmail("test2@email.com");
        accountRepository.save(account2);
        accountIdList.add(account2.getId());

        // Dummy Challenges
        Challenges chal1 = new Challenges();
        chal1.setLongitude(-90.0);
        chal1.setLatitude(41.0);
        chal1.setCreationdate(LocalDate.now());
        chal1.setCreator(account1);
        challengesRepository.save(chal1);
        challengesIdList.add(chal1.getId());

        Challenges chal2 = new Challenges();
        chal2.setLongitude(-90.0);
        chal2.setLatitude(-41.0);
        chal2.setCreationdate(LocalDate.now());
        chal2.setCreator(account2);
        challengesRepository.save(chal2);
        challengesIdList.add(chal2.getId());
    }

    @Test
    @Order(1)
    public void comments_postComment(){
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body("test comment").
                when().
                post("/comments?cid=1&uid=1");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body
        String returnString = response.getBody().asString();
        System.out.println(returnString);
    }

//    @Test
//    @Order(2)
//    public void comments_getComment(){
//
//    }
//
//    @Test
//    @Order(3)
//    public void comments_updateComment(){
//
//    }
//
//    @Test
//    @Order(4)
//    public void comments_deleteComment(){
//
//    }
//
//    @Test
//    @Order(5)
//    public void comments_listCommentsByChallenge(){
//
//    }
//
//    @Test
//    @Order(6)
//    public void comments_listCommentsByUser(){
//
//    }
}
