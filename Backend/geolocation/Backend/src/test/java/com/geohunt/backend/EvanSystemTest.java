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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class EvanSystemTest {

    private AutoCloseable mocks;

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

    @BeforeEach
    public void init(){
        mocks = MockitoAnnotations.openMocks(this);
    }

    @Test
    public void loginTest() {


    }

    @AfterEach
    void tearDown() throws Exception{
        mocks.close();
    }
}
