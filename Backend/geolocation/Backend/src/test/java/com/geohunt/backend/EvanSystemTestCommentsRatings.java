package com.geohunt.backend;

import com.geohunt.backend.comments.Comment;
import com.geohunt.backend.comments.CommentRepository;
import com.geohunt.backend.database.*;
import io.restassured.RestAssured;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
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

        // db setup
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
        // Variables
        String comment = "test comment";
        long cid = challengesIdList.get(1);
        long uid = accountIdList.get(1);

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body(comment).
                when().
                post("/comments?cid=" + cid + "&uid=" + uid);


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body
        String returnString = response.getBody().asString();
        System.out.println(returnString);
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(comment, returnObj.get("comment"));
            assertEquals(uid, returnObj.getJSONObject("author").getLong("id"));
            assertEquals(cid, returnObj.getJSONObject("challenge").getLong("id"));
            commentIdList.add(returnObj.getLong("id"));

        }
        catch (JSONException e){
            e.printStackTrace();
            fail();
        }

        // Check database
        assertTrue(commentRepository.findById(commentIdList.get(0)).isPresent());
    }

    @Test
    @Order(2)
    public void comments_getComment(){
        // Variables
        long commentId = commentIdList.get(0);

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                when().
                get("/comments/" + commentId);


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body
        String returnString = response.getBody().asString();
        System.out.println(returnString);
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(commentId, returnObj.getLong("id"));

        }
        catch (JSONException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(3)
    public void comments_updateComment(){
        // Variables
        String comment = "hello world";
        long commentId = commentIdList.get(0);

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body(comment).
                when().
                put("/comments/" + commentId);


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body
        String returnString = response.getBody().asString();
        System.out.println(returnString);
        try{
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(comment, returnObj.get("comment"));

        }
        catch (JSONException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(4)
    public void comments_deleteComment(){
        // Variables
        long commentId = commentIdList.get(0);

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                when().
                delete("/comments/" + commentId);


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    @Order(5)
    public void addRating(){
        // Variables
        long challengeId = challengesIdList.get(0);
        int rating = 4;

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                when().
                post("/geohunt/rate?cid=" + challengeId + "&rating=" + rating);


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
    }

    @Test
    @Order(6)
    public void getRating(){
        // Variables
        long challengeId = challengesIdList.get(0);
        Challenges challenges = challengesRepository.findById(challengeId).get();
        challenges.addRating(4);
        challenges.addRating(2);
        challenges.addRating(3);
        challengesRepository.save(challenges);

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                when().
                get("/geohunt/challenge/" + challengeId + "/rating");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Verify
        String returnString = response.body().asString();
        double rating = Double.parseDouble(returnString);
        assertEquals(3.25, rating);
    }

    @Test
    @Order(7)
    public void addRating_fail(){
        // Variables
        long challengeId = challengesIdList.get(0);
        int rating = 7;

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                when().
                post("/geohunt/rate?cid=" + challengeId + "&rating=" + rating);


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(400, statusCode);
    }

    @AfterAll
    public void resetRepositories(){
        commentRepository.deleteAll();
        challengesRepository.deleteAll();
        accountRepository.deleteAll();
    }
}
