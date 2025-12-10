package com.geohunt.backend;

import com.geohunt.backend.comments.Comment;
import com.geohunt.backend.comments.CommentRepository;
import com.geohunt.backend.database.*;
import com.geohunt.backend.util.Location;
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
public class EvanSystemTestSubmissions {

    @Autowired
    SubmissionsRepository submissionsRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ChallengesRepository challengesRepository;

    ArrayList<Long> accountIdList;
    ArrayList<Long> challengesIdList;
    ArrayList<Long> submissionsIdList;

    @LocalServerPort
    int port;

    @BeforeAll
    public void setup(){
        // RestAssured setup
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // test db setup
        accountIdList = new ArrayList<Long>();
        challengesIdList = new ArrayList<Long>();
        submissionsIdList = new ArrayList<>();

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

        // Dummy Submissions
        Submissions submission1 = new Submissions();
        submission1.setLongitude(-90.0);
        submission1.setLatitude(-41.0);
        submission1.setSubmitter(account1);
        submission1.setChallenge(chal2);
        submissionsRepository.save(submission1);
        submissionsIdList.add(submission1.getId());

        Submissions submission2 = new Submissions();
        submission2.setLongitude(-90.1);
        submission2.setLatitude(-41.1);
        submission2.setSubmitter(account2);
        submission2.setChallenge(chal2);
        submissionsRepository.save(submission2);
        submissionsIdList.add(submission2.getId());
    }

    @Test
    @Order(1)
    public void submission_createSubmission(){
        try {
            // Variables
            Long account1 = accountIdList.get(0);
            Long challenge1 = challengesIdList.get(0);

            // Submission
            String submission = new JSONObject()
                    .put("longitude", -90.1)
                    .put("latitude", 41.1)
                    .toString();


            // Send request and receive response
            Response response = RestAssured.given().
                    header("Content-Type", "application/json").
                    header("charset", "utf-8").
                    body(submission).
                    when().
                    post("/geohunt/submission?uid=" + account1 + "&cid=" + challenge1);


            // Check status code
            int statusCode = response.getStatusCode();
            assertEquals(200, statusCode);

            // Check response body
            String returnString = response.getBody().asString();
            Location a = new Location(41.0,-90.0);
            Location b = new Location(41.1, -90.1);
            assertEquals(a.distanceMiles(b),Double.parseDouble(returnString));
        }
        catch (JSONException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(2)
    public void submission_getSubmission(){
        try {
            // Variables
            Long submission1 = submissionsIdList.get(0);


            // Send request and receive response
            Response response = RestAssured.given().
                    header("Content-Type", "application/json").
                    header("charset", "utf-8").
                    when().
                    get("/geohunt/submission/" + submission1);


            // Check status code
            int statusCode = response.getStatusCode();
            assertEquals(200, statusCode);

            // Check response body
            String returnString = response.getBody().asString();
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(submission1, returnObj.getLong("id"));
        }
        catch (JSONException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(3)
    public void submission_listSubmissionsChallenge(){
        try {
            // Variables
            Long challenge1 = challengesIdList.get(1);


            // Send request and receive response
            Response response = RestAssured.given().
                    header("Content-Type", "application/json").
                    header("charset", "utf-8").
                    when().
                    get("/geohunt/challenge/" + challenge1 + "/submissions");


            // Check status code
            int statusCode = response.getStatusCode();
            assertEquals(200, statusCode);

            // Check response body
            String returnString = response.getBody().asString();
            JSONArray returnArr = new JSONArray(returnString);
            assertNotNull(returnArr);

        }
        catch (JSONException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(4)
    public void submission_updateSubmission(){
        try {
            // Variables
            Long submission1 = submissionsIdList.get(0);

            // Submission
            String submission = new JSONObject()
                    .put("longitude", -50)
                    .put("latitude", 40)
                    .toString();


            // Send request and receive response
            Response response = RestAssured.given().
                    header("Content-Type", "application/json").
                    header("charset", "utf-8").
                    body(submission).
                    when().
                    put("/geohunt/submission/" + submission1);


            // Check status code
            int statusCode = response.getStatusCode();
            assertEquals(200, statusCode);

            // Check response body
            String returnString = response.getBody().asString();
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(-50, returnObj.getDouble("longitude"));
            assertEquals(40, returnObj.getDouble("latitude"));
        }
        catch (JSONException e){
            e.printStackTrace();
            fail();
        }
    }

    @Test
    @Order(5)
    public void submission_deleteSubmission(){
        // Variables
        Long submission1 = submissionsIdList.get(0);


        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset", "utf-8").
                when().
                delete("/geohunt/submission/" + submission1);


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);
    }
}
