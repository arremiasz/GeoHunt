package com.geohunt.backend;

import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.AccountRepository;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class EvanSystemTest {

    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @LocalServerPort
    int port = 8080;

    @Before
    public void setUp(){
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void signupTest() {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset","utf-8").
                body("{\"username\":\"test1\",\"email\":\"test1@mail.com\",\"password\":\"test1\"}").
                when().
                post("/signup");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body for correct response
        String returnString = response.getBody().asString();

        assertEquals("{\"id\":1}", returnString);

    }
}
