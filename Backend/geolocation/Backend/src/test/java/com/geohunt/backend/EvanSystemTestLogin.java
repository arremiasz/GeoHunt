package com.geohunt.backend;

import com.geohunt.backend.Controllers.LogInController;
import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.database.AccountRepository;
import io.restassured.RestAssured;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class EvanSystemTestLogin {

    private AutoCloseable mocks;

    @InjectMocks
    LogInController logInController;

    @Mock
    AccountService mockAccountService;

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
    public void testLogin(){
    }

    @AfterEach
    void tearDown() throws Exception{
        mocks.close();
    }
}
