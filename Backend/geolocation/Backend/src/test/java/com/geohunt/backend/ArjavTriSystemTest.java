package com.geohunt.backend;



import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.Services.NotificationsService;
import com.geohunt.backend.database.*;
import com.geohunt.backend.powerup.*;
import com.geohunt.backend.util.Location;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.FileInputStream;


import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ArjavTriSystemTest {


    @Autowired
    private PowerupRepository powerupRepository;


    @Autowired
    private PowerupService powerupService;

    @Autowired
    private ChallengesRepository challengesRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private GeohuntService geohuntService;

    @Autowired
    private NotificationsService notificationsService;
    @Autowired
    private NotificationsRepository notificationsRepository;

    @BeforeEach
    void printProfiles(@Autowired org.springframework.core.env.Environment env) {
        System.out.println("ACTIVE PROFILES = " + String.join(", ", env.getActiveProfiles()));
    }

    @BeforeAll
    void setup(@Autowired PowerupRepository repo,
                      @Autowired ChallengesRepository crepo,
                      @Autowired AccountService accountService) {

        Powerup p = new Powerup();
        p.setName("General Hint");
        p.setAffect("NULL");
        p.setType(PowerupEffects.LOCATION_HINT_GENERAL);
        repo.save(p);

        Powerup a = new Powerup();
        a.setName("Specific Hint");
        a.setAffect("NULL");
        a.setType(PowerupEffects.LOCATION_HINT_SPECIFIC);

        repo.save(a);

        Powerup b = new Powerup();
        b.setName("More Minutes");
        b.setAffect("NULL");
        b.setType(PowerupEffects.MINUS_MORE_MINUTES);


        repo.save(b);

        Powerup c = new Powerup();
        c.setName("Minus Minutes");
        c.setAffect("NULL");
        c.setType(PowerupEffects.MINUS_MORE_MINUTES);


        repo.save(c);

        Challenges ch = new Challenges();

        ch.setLongitude(-97.0);
        ch.setLatitude(45.0);
        ch.setCreationdate(LocalDate.now());
        crepo.save(ch);

        Account ac = new Account();
        ac.setUsername("test");
        ac.setEmail("test@gmail.com");
        ac.setPassword("abc");
        accountService.createAccount(ac);

        System.out.println(ac.getId());

        Challenges cha = new Challenges();

        cha.setLongitude(-97.0);
        cha.setLatitude(45.0);
        cha.setCreationdate(LocalDate.now());
        cha.setCreator(ac);
        crepo.save(cha);


        Notifications n = new Notifications();
        n.setMessage("Hello!");
        n.setTarget(ac);
        n.setSentAt(LocalDateTime.now());
        n.setReadStatus(true);
        notificationsRepository.save(n);
    }

    @Order(1)
    @Test
    void forceSchemaCreation() {
        powerupRepository.count();
    }

    @Order(2)
    @Test
    public void testAccount(){

        File imageFile = new File("src/test/java/com/geohunt/backend/cy_logo_05 (1).png");
        byte[] imageBytes = new byte[(int) imageFile.length()];

        try{
            FileInputStream fis = new FileInputStream(imageFile);
            fis.read(imageBytes);
            Account a = accountService.getAccountByUsername("test");
            assertTrue(a.getUsername().equals("test"));
        } catch(Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Order(3)
    @Test
    public void testAccountChange(){
        Account acc = accountService.getAccountByUsername("test");
        Account newAc = new Account();
        newAc.setUsername("testChange");
        accountService.updatedAccount(acc.getId(), newAc);
        assertTrue(accountService.getAccountByUsername("testChange") != null);
    }

    @Order(4)
    @Test
    public void testGeohuntDeleteUsersChallenges(){
        long uid = 1;
        long cid = 2;

        geohuntService.deleteUsersChallenges(uid, cid);

        assertFalse(challengesRepository.existsById(cid));

    }

    @Order(5)
    @Test
    public void testUpdateNotification(){
        long notifid = 1;
        assertNotNull(notificationsService.getNotificationById(notifid));
        Notifications notif = notificationsService.getNotificationById(notifid);
        assertEquals("Hello!", notif.getMessage());
        assertTrue(notif.isReadStatus());
        notificationsService.editNotification(notifid, "hello");
        notif = notificationsService.getNotificationById(notifid);
        assertEquals("hello", notif.getMessage());
        assertFalse(notif.isReadStatus());
    }

    @Order(6)
    @Test
    public void testPowerupGenerationRandom(){
        long challengeId = 1;
        Location start = new Location(42.033860, -93.642763);

        ResponseEntity result = powerupService.generate(challengeId, start);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        RandomGenerationDTO dto = (RandomGenerationDTO) result.getBody();
        assertNotNull(dto);
        assertNotNull(dto.getPowerup());

        Location generated = new Location(dto.getLat(), dto.getLon());
        Location end = new Location(45.0, -97.0);

        double totalDist = start.distanceKM(end);
        double distStartToGen = start.distanceKM(generated);
        double ratio = distStartToGen / totalDist;

        assertTrue(ratio >= 0.30 && ratio <= 0.70,
                "Generated point must be within 30–70% toward the goal");
    }

    @Order(7)
    @Test
    public void testTimeDecrease(){
        long chalId = 1;
        Location start = new Location(42.033860, -93.642763);
        Location pup = new Location(42.04048112, -93.644773389);

        int result = powerupService.deduceTimeDeduction(chalId, start, pup);

        assertTrue(result > 20, "Time deduction must always be greater than base time");
        assertTrue(result < 10000, "Should not produce absurdly large values. Produced: " + result);

        Location end = new Location(45.0, -97.0);

        double totalDist = start.distanceKM(end) * 1000;
        double offset = powerupService.perpendicularDistance(start, end, pup);

        double expected = 20 + (totalDist * 0.02) + (offset * 0.01);
        int expectedRounded = (int) Math.round(expected);

        assertEquals(expectedRounded, result,
                "Time deduction calculation must match formula exactly");
    }

    @Order(8)
    @Test
    public void generalClue() {
        int chalId = 1;
        String clue = powerupService.generalClue(chalId);

        assertNotNull(clue);
        assertFalse(clue.contains("Error"));
        assertFalse(clue.isBlank());

        System.out.println(clue);
    }

    @Order(9)
    @Test
    public void specificClue(){
        int chalId = 1;
        String clue = powerupService.specificClue(chalId);

        assertNotNull(clue);
        assertFalse(clue.contains("Error"));
        assertFalse(clue.isBlank());

        System.out.println(clue);

    }
}
