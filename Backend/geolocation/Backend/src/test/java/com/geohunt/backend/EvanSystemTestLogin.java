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
public class EvanSystemTestLogin {

    @Autowired
    AccountRepository accountRepository;

    ArrayList<Long> accountIdList;

    @LocalServerPort
    int port;

    @BeforeAll
    public void setup(){
        // RestAssured setup
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";

        // test db setup
        accountIdList = new ArrayList<Long>();
        accountRepository.deleteAll();

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
    }

    @Test
    @Order(1)
    public void login_success(){
        // Variables
        Account account = accountRepository.findById(accountIdList.get(0)).get();
        String username = account.getUsername();
        String password = account.getPassword();


        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}").
                when().
                post("/login");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body
        String returnString = response.getBody().asString();
        assertEquals(accountIdList.get(0),Long.parseLong(returnString));
    }

    @Test
    @Order(2)
    public void login_incorrectUsername(){
        // Variables
        Account account = accountRepository.findById(accountIdList.get(0)).get();
        String username = account.getUsername() + "incorrect";
        String password = account.getPassword();


        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}").
                when().
                post("/login");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(400, statusCode);
    }

    @Test
    @Order(3)
    public void login_incorrectPassword(){
        // Variables
        Account account = accountRepository.findById(accountIdList.get(0)).get();
        String username = account.getUsername();
        String password = account.getPassword() + "incorrect";


        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}").
                when().
                post("/login");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(400, statusCode);
    }

    @AfterAll
    public void resetRepositories(){
        accountRepository.deleteAll();
    }
}
