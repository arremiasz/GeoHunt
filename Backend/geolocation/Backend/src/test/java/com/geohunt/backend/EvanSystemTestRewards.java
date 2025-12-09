package com.geohunt.backend;



import com.geohunt.backend.database.*;
import com.geohunt.backend.rewards.Customization;
import com.geohunt.backend.rewards.Reward;
import com.geohunt.backend.rewards.RewardRepository;
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
public class EvanSystemTestRewards {

    @Autowired
    RewardRepository rewardRepository;

    @Autowired
    ChallengesRepository challengesRepository;

    @Autowired
    SubmissionsRepository submissionsRepository;

    @Autowired
    AccountRepository accountRepository;

    ArrayList<Long> rewardIdList;
    ArrayList<Long> challengesIdList;
    ArrayList<Long> submissionsIdList;
    ArrayList<Long> accountIdList;

    @LocalServerPort
    int port;

    @BeforeAll
    public void setup(){
        // RestAssured setup
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // db setup
        accountIdList = new ArrayList<>();
        challengesIdList = new ArrayList<>();
        rewardIdList = new ArrayList<>();
        submissionsIdList = new ArrayList<>();

        accountRepository.deleteAll();
        challengesRepository.deleteAll();
        submissionsRepository.deleteAll();

        // Account

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

        // Challenges

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

        // Submissions
        Submissions sub1 = new Submissions();
        sub1.setChallenge(chal1);
        sub1.setSubmitter(account1);
        sub1.setLongitude(-90.1);
        sub1.setLatitude(41.1);
        submissionsRepository.save(sub1);
        submissionsIdList.add(sub1.getId());

        Submissions sub2 = new Submissions();
        sub2.setChallenge(chal2);
        sub2.setSubmitter(account2);
        sub2.setLongitude(-90.1);
        sub2.setLatitude(-41.1);
        submissionsRepository.save(sub2);
        submissionsIdList.add(sub2.getId());

        // Rewards
        Customization customization1 = new Customization();
        customization1.setName("custom1");
        customization1.setValue(500);
        rewardRepository.save(customization1);
        rewardIdList.add(customization1.getId());
    }

    @Test
    @Order(1)
    public void rewards_gradeSubmission(){
        // Variables
        Long sid = submissionsIdList.get(1);
        Long uid = accountIdList.get(1);

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                when().
                get("/gradesubmission?sid=" + sid + "&uid=" + uid);


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body
        String returnString = response.getBody().asString();
        System.out.println(returnString);
        try{
            JSONObject returnObj = new JSONObject(returnString);

        }
        catch (JSONException e){
            e.printStackTrace();
            fail();
        }

    }

    @AfterAll
    public void resetRepositories(){
        accountRepository.deleteAll();
        challengesRepository.deleteAll();
        submissionsRepository.deleteAll();
    }
}
