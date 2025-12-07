package com.geohunt.backend;


import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.Services.NotificationsService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.ChallengesRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileInputStream;


import java.io.File;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ArjavTriSystemTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private GeohuntService geohuntService;

    @Autowired
    private ChallengesRepository challengesRepository;

    @Autowired
    private NotificationsService notificationsService;


    @Test
    public void testAccount(){

        File imageFile = new File("src/test/java/com/geohunt/backend/cy_logo_05 (1).png");
        byte[] imageBytes = new byte[(int) imageFile.length()];

        try{
            FileInputStream fis = new FileInputStream(imageFile);
            fis.read(imageBytes);
            Account acc = new Account();
            acc.setUsername("arjavTriSystemTest");
            acc.setPassword("arjavTriSystemTest");
            acc.setEmail("arjavTriSystemTest@gmail.com");
            acc.setPfp(Base64.getEncoder().encodeToString(imageBytes));
            accountService.createAccount(acc);
            Account a = accountService.getAccountByUsername("arjavTriSystemTest");
            assertTrue(a.getUsername().equals("arjavTriSystemTest"));
        } catch(Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void testAccountChange(){
        Account acc = new Account();
        acc.setUsername("arjavTriSystemTestChange");
        accountService.updatedAccount((long) 46, acc);
        assertTrue(accountService.getAccountByUsername("arjavTriSystemTestChange") != null);
    }

    @Test
    public void testGeohuntDeleteUsersChallenges(){
        long uid = 36;
        long cid = 79;

        geohuntService.deleteUsersChallenges(uid, cid);

        assertFalse(challengesRepository.existsById(cid));

    }

    @Test
    public void testUpdateNotification(){
        long notifid = 1;
        assertNotNull(notificationsService.getNotificationById(notifid));
        assertEquals("changed.", notificationsService.getNotificationById(notifid).getMessage());
        notificationsService.editNotification(notifid, "heyyy");
        assertEquals("heyyy", notificationsService.getNotificationById(notifid).getMessage());


    }
}
