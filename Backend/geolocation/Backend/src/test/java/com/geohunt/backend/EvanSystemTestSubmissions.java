package com.geohunt.backend;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class EvanSystemTestSubmissions {

    private AutoCloseable mocks;

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
    public void testSaveSubmission(){

    }

    @Test
    public void testGetSubmission(){

    }

    @Test
    public void testUpdateSubmission(){

    }

    @Test
    public void testDeleteSubmission(){

    }

    @Test
    public void testListSubmissionsWithUser(){

    }

    @Test
    public void testListSubmissionsWithChallenge(){

    }

    @AfterEach
    void tearDown() throws Exception{
        mocks.close();
    }
}
