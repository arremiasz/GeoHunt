package com.geohunt.backend;


import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.Services.NotificationsService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.ChallengesRepository;
import com.geohunt.backend.powerup.Powerup;
import com.geohunt.backend.powerup.PowerupService;
import com.geohunt.backend.powerup.RandomGenerationDTO;
import com.geohunt.backend.util.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

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

    @Autowired
    private PowerupService powerupService;

    @Test
    public void testAccount(){

        File imageFile = new File("src/test/java/com/geohunt/backend/cy_logo_05 (1).png");
        byte[] imageBytes = new byte[(int) imageFile.length()];

        try{
            FileInputStream fis = new FileInputStream(imageFile);
            fis.read(imageBytes);
            Account acc = new Account();
            acc.setUsername("arjavTriSystem");
            acc.setPassword("arjavTriSystemTest");
            acc.setEmail("arjavTriSystemTest2@gmail.com");
            acc.setPfp(Base64.getEncoder().encodeToString(imageBytes));
            accountService.createAccount(acc);
            Account a = accountService.getAccountByUsername("arjavTriSystem");
            assertTrue(a.getUsername().equals("arjavTriSystem"));
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
        assertEquals("hey", notificationsService.getNotificationById(notifid).getMessage());
        notificationsService.editNotification(notifid, "hello");
        assertEquals("hello", notificationsService.getNotificationById(notifid).getMessage());
    }

    @Test
    public void testPowerupGenerationRandom(){
        ResponseEntity a = powerupService.get("general_hint");
        HttpStatus s = (HttpStatus) a.getStatusCode();
        if(s == HttpStatus.NOT_FOUND){
            Assertions.fail("Cant get powerup from DB");
        }
        Powerup found = (Powerup) a.getBody();
        assertEquals(found.getId(), 1);

        ResponseEntity gen = powerupService.generate(254, new Location(42.033860, -93.642763));
        if(gen.getStatusCode() != HttpStatus.CREATED){
            Assertions.fail("Cant generate random powerup");
        }
        RandomGenerationDTO rgd = (RandomGenerationDTO) gen.getBody();
        System.out.println(rgd.getPowerup().getName());
        System.out.println(rgd.getLat());
        System.out.println(rgd.getLon());
        assertTrue(true);
    }
}
