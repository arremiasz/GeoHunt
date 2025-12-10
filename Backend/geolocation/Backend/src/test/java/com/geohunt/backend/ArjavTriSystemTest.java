package com.geohunt.backend;



import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.Services.NotificationsService;
import com.geohunt.backend.Shop.*;
import com.geohunt.backend.Shop.DTOs.TransactionDTO;
import com.geohunt.backend.database.*;
import com.geohunt.backend.powerup.*;
import com.geohunt.backend.util.Location;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


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
    private AccountRepository accountRepository;

    @Autowired
    private GeohuntService geohuntService;

    @Autowired
    private NotificationsService notificationsService;

    @Autowired
    private NotificationsRepository notificationsRepository;

    @Autowired
    private FriendsService friendsService;

    @Autowired
    ShopService shopService;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    UserInventoryRepository uiRepo;

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionsRepository transactionsRepository;


    private long uid;
    private long uid2;
    private long cid;
    private long pid;
    private long nid;


    @BeforeAll
    void setup(@Autowired PowerupRepository repo,
                      @Autowired ChallengesRepository crepo,
                      @Autowired AccountService accountService) {

        Powerup p = new Powerup();
        p.setName("General Hint");
        p.setAffect("NULL");
        p.setType(PowerupEffects.LOCATION_HINT_GENERAL);
        repo.save(p);
        pid = p.getId();

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
        cid = ch.getId();

        Account ac = new Account();
        ac.setUsername("test");
        ac.setEmail("test@gmail.com");
        ac.setPassword("abc");
        accountService.createAccount(ac);
        uid = ac.getId();

        Account ac2 = new Account();
        ac2.setUsername("t");
        ac2.setEmail("t@gmail.com");
        ac2.setPassword("abc");
        accountService.createAccount(ac2);
        uid2 = ac2.getId();


        System.out.println(ac.getId());

        Challenges cha = new Challenges();

        cha.setLongitude(-90.0);
        cha.setLatitude(41.0);
        cha.setCreationdate(LocalDate.now());
        cha.setCreator(ac);
        crepo.save(cha);


        Notifications n = new Notifications();
        n.setMessage("Hello!");
        n.setTarget(ac);
        n.setSentAt(LocalDateTime.now());
        n.setReadStatus(true);
        notificationsRepository.save(n);
        nid = n.getId();
    }

    @Order(1)
    @Test
    void forceSchemaCreation() {
        powerupRepository.count();
    }

    @Test
    @Order(2)
    public void testGetAccountByUsername_returnsAccount() {
        Account a = accountService.getAccountByUsername("test");
        assertNotNull(a);
        assertEquals("test", a.getUsername());
    }

    @Test
    @Order(3)
    public void testGetIdByUsername_returnsCorrectId() {
        long foundId = accountService.getIdByUsername("test");
        assertEquals(uid, foundId);
    }

    @Test
    @Order(4)
    public void testGetAccountById_returnsCorrectAccount() {
        Account a = accountService.getAccountById(uid);
        assertNotNull(a);
        assertEquals(uid, a.getId());
        assertEquals("test", a.getUsername());
    }

    @Test
    @Order(5)
    public void testGetAccountById_throwsOnMissingId() {
        assertThrows(IllegalArgumentException.class, () ->
                accountService.getAccountById((long)999999)
        );
    }

    @Test
    @Order(6)
    public void testCreateAccount_success() {
        Account a2 = new Account();
        a2.setUsername("newUser");
        a2.setEmail("new@gmail.com");
        a2.setPassword("abc");

        long newId = accountService.createAccount(a2);

        assertTrue(newId > 0);
        assertEquals(newId, accountService.getIdByUsername("newUser"));
    }

    @Test
    @Order(7)
    public void testCreateAccount_emailConflict() {
        Account dup = new Account();
        dup.setUsername("uniqueUser");
        dup.setEmail("test@gmail.com");
        dup.setPassword("abc");

        long result = accountService.createAccount(dup);
        assertEquals(-2, result);
    }

    @Test
    @Transactional
    @Order(8)
    public void testDeleteAccountById_deletesSuccessfully() {
        Account acc = new Account();
        acc.setUsername("tempUser");
        acc.setEmail("temp@gmail.com");
        acc.setPassword("abc");
        accountService.createAccount(acc);

        long id = acc.getId();

        boolean deleted = accountService.deleteAccountByID(id);
        assertTrue(deleted);

        assertThrows(IllegalArgumentException.class, () ->
                accountService.getAccountById(id)
        );
    }

    @Test
    @Order(9)
    public void testDeleteAccountById_missingUserReturnsFalse() {
        boolean result = accountService.deleteAccountByID((long) 123456);
        assertFalse(result);
    }

    @Test
    @Order(10)
    public void testUpdatedAccount_updatesUsername() {
        Account acc = new Account();
        acc.setUsername("oldName");
        acc.setEmail("old@gmail.com");
        acc.setPassword("abc");
        accountService.createAccount(acc);

        Account update = new Account();
        update.setUsername("newName");

        accountService.updatedAccount(acc.getId(), update);

        Account updated = accountService.getAccountByUsername("newName");
        assertNotNull(updated);
    }

    @Test
    @Order(11)
    public void testDeleteAccountByUsername_deletesSuccessfully() {
        Account acc = new Account();
        acc.setUsername("deleteMe");
        acc.setEmail("deleteMe@gmail.com");
        acc.setPassword("abc");
        accountService.createAccount(acc);

        boolean deleted = accountService.deleteAccountByUsername("deleteMe");
        assertTrue(deleted);
    }

    @Test
    @Order(12)
    public void testUpdatedAccount_duplicateUsernameConflict() {
        Account a1 = new Account();
        a1.setUsername("userA");
        a1.setEmail("a@gmail.com");
        a1.setPassword("abc");
        accountService.createAccount(a1);

        Account a2 = new Account();
        a2.setUsername("userB");
        a2.setEmail("b@gmail.com");
        a2.setPassword("abc");
        accountService.createAccount(a2);

        // Try to rename userB to userA
        Account update = new Account();
        update.setUsername("userA"); // duplicate

        ResponseEntity<String> response =
                accountService.updatedAccount(a2.getId(), update);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }


    @Test
    @Order(13)
    public void testGeohuntDeleteUsersChallenges() {
        double lat = 40.0;
        double lon = -91.0;
        String url = "delete-me-url";

        ResponseEntity resp = geohuntService.customChallenge(lat, lon, uid, url);
        assertEquals(HttpStatus.OK, resp.getStatusCode());

        Challenges created = (Challenges) resp.getBody();
        assertNotNull(created);
        long newCid = created.getId();

        ResponseEntity deleteResp = geohuntService.deleteUsersChallenges(uid, newCid);

        assertEquals(HttpStatus.OK, deleteResp.getStatusCode());
        assertFalse(challengesRepository.existsById(newCid));
    }

    @Test
    @Order(14)
    public void testSaveNotification_savesCorrectly() {
        Account a = accountService.getAccountByUsername("test");

        notificationsService.saveNotification(a, "unitTestMsg");

        Notifications saved = notificationsRepository.getNotificationsByMessage("unitTestMsg");
        assertNotNull(saved);
        assertEquals("unitTestMsg", saved.getMessage());
        assertEquals(a.getId(), saved.getTarget().getId());
        assertFalse(saved.isReadStatus());
    }

    @Test
    @Order(15)
    public void testMarkAsRead_updatesReadStatus() {
        Notifications n = notificationsRepository.getNotificationsByMessage("Hello!");
        assertNotNull(n);

        notificationsService.markAsRead(n.getId());

        Notifications updated = notificationsRepository.findById(n.getId()).orElseThrow();
        assertTrue(updated.isReadStatus());
    }

    @Test
    @Order(16)
    public void testDeleteNotification(){
        List<Notifications> not = notificationsService.getMyNotifs(uid);
        assertFalse(not.isEmpty());
        assertTrue(not.size() == 2); //2 if all tests run due to previous code. 1 if only this one.


        Account a2 = accountService.getAccountById(uid2);

        notificationsService.sendNotificationToUser(a2.getUsername(), "test2");
        assertTrue((notificationsService.getMyNotifs(a2.getId()).size() == 1));

        notificationsService.deleteNotification(notificationsRepository.getNotificationsByMessage("test2").getId());
        assertTrue((notificationsService.getMyNotifs(a2.getId()).size() == 0));
    }

    @Test
    @Order(17)
    public void testGetMyNotifs_returnsCorrectList() {
        List<Notifications> notifs = notificationsService.getMyNotifs(uid);
        assertNotNull(notifs);
        assertFalse(notifs.isEmpty());
    }

    @Test
    @Order(18)
    public void testDeleteNotification_removesNotification() {
        Account user = accountService.getAccountById(uid2);

        notificationsService.sendNotificationToUser(user.getUsername(), "deleteTest");

        Notifications n = notificationsRepository.getNotificationsByMessage("deleteTest");
        assertNotNull(n);

        notificationsService.deleteNotification(n.getId());

        assertFalse(notificationsRepository.existsById(n.getId()));
    }

    @Test
    @Order(19)
    public void testEditNotification_updatesMessageAndResetsReadStatus() {
        Notifications n;
        try{
            n = notificationsRepository.findById(nid).orElseThrow();
            assertTrue(n.isReadStatus());
        } catch (Exception e){
            Assertions.fail(e.getMessage());
        }
        notificationsService.editNotification(nid, "editedMsg");
        Notifications updated;
        try{
            updated = notificationsRepository.findById(nid).orElseThrow();
            assertEquals("editedMsg", updated.getMessage());
            assertFalse(updated.isReadStatus());
        } catch (Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Order(20)
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

    @Order(21)
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

    @Order(22)
    @Test
    public void generalClue() {
        int chalId = 1;
        String clue = powerupService.generalClue(chalId);

        assertNotNull(clue);
        assertFalse(clue.contains("Error"));
        assertFalse(clue.isBlank());

        System.out.println(clue);
    }

    @Order(23)
    @Test
    public void specificClue(){
        int chalId = 1;
        String clue = powerupService.specificClue(chalId);

        assertNotNull(clue);
        assertFalse(clue.contains("Error"));
        assertFalse(clue.isBlank());

        System.out.println(clue);
    }

    @Test
    @Order(24)
    public void testHaversine_zeroDistance() {
        double zero = 0;
        double d = geohuntService.haversine(zero, zero, zero, zero);
        assertEquals(zero, d, 0.001);
    }

    @Test
    @Order(25)
    public void testHaversine_knownDistance() {
        double lat1 = 43;
        double lon1 = -97;
        double lat2 = 43.239723;
        double lon2 = -96.242;

        double ans = geohuntService.haversine(lat1, lon1, lat2, lon2);
        assertEquals(41.664524189723, ans, 0.0001);
    }

    @Test
    @Order(26)
    public void testGetPossibleChallenges_closeToOne() {
        double lat1, lon1, r1;
        lat1 = 41.01;
        lon1 = -90.01;
        r1 = 2;
        List<Challenges> c = geohuntService.getPossibleChallenges(lat1, lon1, r1);
        assertEquals(1, c.size());

    }

    @Test
    @Order(27)
    public void testGetPossibleChallenges_closeToTwo() {
        double lat1, lon1, r1;
        lat1 = 41.01;
        lon1 = -90.01;
        r1 = 1000;
        List<Challenges> c = geohuntService.getPossibleChallenges(lat1, lon1, r1);
        assertEquals(2, c.size());

    }

    @Test
    @Order(28)
    public void testGetPossibleChallenges_closeToNone() {
        double lat1, lon1, r1;

        lat1 = 41.01;
        lon1 = -90.01;
        r1 = 0.5;
        List<Challenges> c = geohuntService.getPossibleChallenges(lat1, lon1, r1);
        assertEquals(0, c.size());

    }
    @Test
    @Order(29)
    public void testGetPossibleChallenges_ExactlyOnOne() {
        double lat1, lon1, r1;

        lat1 = 41;
        lon1 = -90;
        r1 = 0;
        List<Challenges> c = geohuntService.getPossibleChallenges(lat1, lon1, r1);
        assertEquals(1, c.size());

    }

    @Test
    @Order(30)
    public void testGetChallenge_Distance() {
        double lat1, lon1, r1;

        lat1 = 40;
        lon1 = -91;
        r1 = 0.5;
        Challenges c = geohuntService.getChallenge(lat1, lon1, r1);
        double haversine = geohuntService.haversine(lat1, lon1, c.getLatitude(), c.getLongitude());
        assertNotNull(c);
        assertTrue(c.getId() > 0);
    }

    @Test
    @Order(31)
    public void testGetChallenge_NotNull() {
        double lat1, lon1, r1;

        lat1 = 40;
        lon1 = -91;
        r1 = 10;
        Challenges c = geohuntService.getChallenge(lat1, lon1, r1);
        assertTrue(c.getId() > -1);

    }

    @Test
    @Order(32)
    public void testFallbackGenerate_CountAndInDB() {
        double lat1, lon1, r1;
        lat1 = 40;
        lon1 = -91;
        r1 = 10;
        List<Challenges> cFB = geohuntService.fallbackGenerate(lat1, lon1, r1, 2);
        assertTrue(cFB.size() == 2);
        boolean found = true;
        for(int i = 0; i < cFB.size(); i++) {
            Challenges c = cFB.get(i);
            long cfbid = c.getId();
            Optional<Challenges> ch = challengesRepository.findById(cfbid);
            assertTrue(ch.isPresent());
        }
    }

    @Test
    @Order(33)
    public void testCustomChallenge_Complete() {
        double lat1, lon1;
        String url = "www"+ UUID.randomUUID();;

        lat1 = 40;
        lon1 = -91;

        ResponseEntity a = geohuntService.customChallenge(lat1, lon1, uid, url);

        assertEquals(a.getStatusCode(), HttpStatus.OK);
        try{
            Challenges c = (Challenges) a.getBody();
            assertTrue(c.getLatitude() == lat1);
            assertTrue(c.getLongitude() == lon1);
            assertTrue(c.getStreetviewurl().equals(url));
            assertTrue(c.getCreator().getId() == uid);
        } catch (Exception e){
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    @Order(34)
    public void testUsersChallenges_andDelete() {
        double lat1, lon1;
        String url = "www"+ UUID.randomUUID();;

        lat1 = 40;
        lon1 = -91;

        ResponseEntity a = geohuntService.customChallenge(lat1, lon1, uid, url);

        assertEquals(a.getStatusCode(), HttpStatus.OK);

        ResponseEntity b = geohuntService.getUsersChallenges(uid);
        assertEquals(b.getStatusCode(), HttpStatus.OK);
        List<Challenges> bc = (List) b.getBody();

        assertTrue(bc.size() == 3);

        ResponseEntity delete = geohuntService.deleteUsersChallenges(uid, bc.get(0).getId());
        assertEquals(HttpStatus.OK, delete.getStatusCode());

        ResponseEntity after = geohuntService.getUsersChallenges(uid);
        List<Challenges> listAfter = (List) after.getBody();
        assertTrue(listAfter.size() == 2);
    }

    @Test
    @Order(35)
    public void friendsdoesAccountsExist_ValidAndNotValid(){
        assertEquals(2, friendsService.doesAccountsExist(uid, uid2).stream().count());
        assertEquals(0, friendsService.doesAccountsExist(uid, 200).stream().count());
        assertEquals(0, friendsService.doesAccountsExist(100, 101).stream().count());
    }

    @Test
    @Transactional
    @Order(36)
    public void friendAdd_AndDelete(){
        Account acc = new Account();
        acc.setUsername("username");
        acc.setPassword("password");
        acc.setEmail("email");

        Account acc2 = new Account();
        acc2.setUsername("username2");
        acc2.setPassword("password2");
        acc2.setEmail("email2");

        accountService.createAccount(acc);
        accountService.createAccount(acc2);

        friendsService.addFriend(acc2.getId(), acc.getId());

        ResponseEntity<List<Account>> friends = friendsService.getFriendRequestsRecieved(acc.getId());
        assertEquals(HttpStatus.OK, friends.getStatusCode());
        assertTrue(friends.getBody().size() == 1);
        assertEquals((friends.getBody().get(0).getUsername()), acc2.getUsername());

        ResponseEntity<List<Account>> friend = friendsService.getFriendRequestsSent(acc2.getId());
        assertEquals(HttpStatus.OK, friend.getStatusCode());
        assertTrue(friend.getBody().size() == 1);
        assertEquals((friend.getBody().get(0).getUsername()), acc.getUsername());

        accountService.deleteAccountByID(acc.getId());
        accountService.deleteAccountByID(acc2.getId());

        friend = friendsService.getFriendRequestsSent(acc2.getId());
        assertTrue(friend.getBody().size() == 0);
    }

    @Test
    @Transactional
    @Order(37)
    public void friendAccept(){
        Account acc = new Account();
        acc.setUsername("username");
        acc.setPassword("password");
        acc.setEmail("email");

        Account acc2 = new Account();
        acc2.setUsername("username2");
        acc2.setPassword("password2");
        acc2.setEmail("email2");

        accountService.createAccount(acc);
        accountService.createAccount(acc2);

        friendsService.addFriend(acc2.getId(), acc.getId());

        friendsService.acceptFriend(acc2.getId(), acc.getId());

        ResponseEntity<List<Account>> friends = friendsService.getFriendRequestsRecieved(acc.getId());
        assertEquals(HttpStatus.OK, friends.getStatusCode());
        assertTrue(friends.getBody().size() == 0);

        ResponseEntity<List<Account>> friendacc = friendsService.getFriends(acc.getId());
        assertEquals(HttpStatus.OK, friendacc.getStatusCode());
        assertTrue(friendacc.getBody().size() == 1);

        accountService.deleteAccountByID(acc.getId());
        accountService.deleteAccountByID(acc2.getId());

        friendacc = friendsService.getFriends(acc.getId());
        assertEquals(HttpStatus.NOT_FOUND, friendacc.getStatusCode());
        assertTrue(friendacc.getBody().size() == 0);
    }

    @Test
    @Order(38)
    public void powerupAdd_AndDelete(){
        Powerup up = new Powerup();
        up.setName("General Hint");
        up.setAffect("whatever");
        up.setType(PowerupEffects.OTHER);
        ResponseEntity error = powerupService.add(up);
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());

        up.setName("testingtesting");
        ResponseEntity fine = powerupService.add(up);
        assertEquals(HttpStatus.CREATED, fine.getStatusCode());
        long id = (long)fine.getBody();
        ResponseEntity fine2 = powerupService.get("testingtesting");
        assertEquals(HttpStatus.OK, fine2.getStatusCode());
        Powerup id2 = (Powerup) fine2.getBody();
        assertEquals(id, id2.getId());

        ResponseEntity del = powerupService.delete(id);
        assertEquals(HttpStatus.OK, del.getStatusCode());

        ResponseEntity del2 = powerupService.delete((long)100);
        assertEquals(HttpStatus.NOT_FOUND, del2.getStatusCode());
    }

    @Test
    @Order(39)
    public void powerupGetId(){

        Powerup up = new Powerup();
        up.setName("testingtesting");
        ResponseEntity fine = powerupService.add(up);
        assertEquals(HttpStatus.CREATED, fine.getStatusCode());
        long id = (long)fine.getBody();
        ResponseEntity fine2 = powerupService.get(id);
        assertEquals(HttpStatus.OK, fine2.getStatusCode());
        Powerup id2 = (Powerup)fine2.getBody();
        assertEquals(id, id2.getId());

        ResponseEntity del = powerupService.delete(id);
        assertEquals(HttpStatus.OK, del.getStatusCode());

    }

    @Test
    @Order(40)
    public void powerupDeduce(){
        Powerup up = new Powerup();

        up.setName("testingtesting");
        up.setType(PowerupEffects.LOCATION_HINT_SPECIFIC);
        ResponseEntity fine = powerupService.add(up);
        assertEquals(HttpStatus.CREATED, fine.getStatusCode());
        long id = up.getId();

        Powerup up2 = new Powerup();

        up2.setName("testingtestinggg");
        up2.setType(PowerupEffects.MINUS_MINUTES);
        ResponseEntity fine2 = powerupService.add(up2);
        assertEquals(HttpStatus.CREATED, fine2.getStatusCode());
        long id2 = up2.getId();



        ResponseEntity deduce = powerupService.deduce(id, uid, cid, new Location(42, -97), new Location(42.05, -96.99));
        assertEquals(HttpStatus.OK, deduce.getStatusCode());
        PowerupEffectDTO p = (PowerupEffectDTO) deduce.getBody();
        System.out.println(p.getLocationName());



        deduce = powerupService.deduce(id2, uid, cid, new Location(42, -97), new Location(42.05, -96.99));
        assertEquals(HttpStatus.OK, deduce.getStatusCode());
        PowerupEffectDTO p2 = (PowerupEffectDTO) deduce.getBody();
        System.out.println(p2.getTimeDecreaseInSeconds());

        ResponseEntity del = powerupService.delete(id);
        assertEquals(HttpStatus.OK, del.getStatusCode());

        ResponseEntity del2 = powerupService.delete((long)100);
        assertEquals(HttpStatus.NOT_FOUND, del2.getStatusCode());
    }

    @Test
    @Transactional
    @Order(41)
    public void testDeleteAccount_RemovesPowerupAssociations() {
        Account acc = new Account();
        acc.setUsername("delUser");
        acc.setPassword("pass");
        acc.setEmail("delUser@gmail.com");
        accountService.createAccount(acc);
        long delUid = acc.getId();

        Powerup pu = new Powerup();
        pu.setName("TestPU-" + UUID.randomUUID());
        pu.setAffect("NULL");
        pu.setType(PowerupEffects.OTHER);
        powerupRepository.save(pu);
        long puId = pu.getId();

        ResponseEntity addResp = powerupService.addToAcc(puId, delUid);
        assertEquals(HttpStatus.CREATED, addResp.getStatusCode());

        Account loaded = accountService.getAccountById(delUid);
        assertTrue(loaded.getPowerups().contains(pu));
        assertTrue(pu.getAccounts().contains(loaded));

        boolean deleted = accountService.deleteAccountByID(delUid);
        assertTrue(deleted);

        assertThrows(IllegalArgumentException.class, () ->
                accountService.getAccountById(delUid));

        Powerup reloadedPU = powerupRepository.findById(puId).orElseThrow();
        assertFalse(reloadedPU.getAccounts().stream()
                        .anyMatch(a -> a.getId() == delUid),
                "Powerup still contains deleted account");

        Set<Account> ownersSet = powerupService.getPowerupOwners(puId);
        assertNotNull(ownersSet);
        assertTrue(ownersSet.isEmpty());
    }

    @Test
    @Transactional
    @Order(42)
    public void testDeleteAccount_NoPowerupsStillDeletes() {
        Account acc = new Account();
        acc.setUsername("nopower");
        acc.setPassword("pass");
        acc.setEmail("nopower@gmail.com");
        accountService.createAccount(acc);
        long id = acc.getId();

        Account loaded = accountService.getAccountById(id);
        assertTrue(loaded.getPowerups().isEmpty());

        boolean deleted = accountService.deleteAccountByID(id);
        assertTrue(deleted);

        assertThrows(IllegalArgumentException.class, () ->
                accountService.getAccountById(id));
    }

    @Test
    @Transactional
    @Order(44)
    public void testDeleteAccount_RemovesMultiplePowerups() {
        Account acc = new Account();
        acc.setUsername("multiPU");
        acc.setPassword("pass");
        acc.setEmail("multiPU@gmail.com");
        accountService.createAccount(acc);
        long id = acc.getId();

        Powerup a = new Powerup();
        a.setName("P-UA-" + UUID.randomUUID());
        a.setType(PowerupEffects.OTHER);
        a.setAffect("NULL");
        powerupRepository.save(a);

        Powerup b = new Powerup();
        b.setName("P-UB-" + UUID.randomUUID());
        b.setType(PowerupEffects.OTHER);
        b.setAffect("NULL");
        powerupRepository.save(b);

        powerupService.addToAcc(a.getId(), id);
        powerupService.addToAcc(b.getId(), id);

        Account loaded = accountService.getAccountById(id);
        assertEquals(2, loaded.getPowerups().size());

        boolean deleted = accountService.deleteAccountByID(id);
        assertTrue(deleted);

        assertFalse(a.getAccounts().stream().anyMatch(ac -> ac.getId() == id));
        assertFalse(b.getAccounts().stream().anyMatch(ac -> ac.getId() == id));
    }

    @Test
    @Transactional
    @Order(45)
    public void testPurchase_Success() {
        Account acc = new Account();
        acc.setUsername("buyer1");
        acc.setPassword("pw");
        acc.setEmail("email1");
        accountService.createAccount(acc);
        accountService.giveAccountMoney(acc, (long) 500);

        Shop item = new Shop();
        item.setName("Test Item");
        item.setPrice(100);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        ResponseEntity<String> result = shopService.purchase(acc.getId(), item.getId());

        assertEquals(200, result.getStatusCodeValue());
        assertTrue(result.getBody().contains("transaction id"));

        Account updated = accountRepository.findById(acc.getId()).get();
        assertEquals(400, updated.getTotalPoints());

        accountService.deleteAccountByID(acc.getId());
    }

    @Test
    @Order(46)
    public void testPurchase_AccountNotFound() {
        Shop item = new Shop();
        item.setName("AAA");
        item.setPrice(50);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        ResponseEntity<String> result = shopService.purchase(999999, item.getId());
        assertEquals(404, result.getStatusCodeValue());
        shopRepository.delete(item);
    }

    @Test
    @Transactional
    @Order(47)
    public void testPurchase_ItemNotFound() {
        Account acc = new Account();
        acc.setUsername("buyer1");
        acc.setPassword("pw");
        acc.setEmail("email1");
        acc.setTotalPoints(100);
        accountRepository.save(acc);

        ResponseEntity<String> result = shopService.purchase(acc.getId(), 999999);
        assertEquals(404, result.getStatusCodeValue());

        accountService.deleteAccountByID(acc.getId());
    }

    @Test
    @Transactional
    @Order(48)
    public void testPurchase_NotEnoughPoints() {
        Account acc = new Account();
        acc.setUsername("buyer1");
        acc.setPassword("pw");
        acc.setEmail("email1");
        acc.setTotalPoints(5);
        accountRepository.save(acc);

        Shop item = new Shop();
        item.setName("Expensive item");
        item.setPrice(100);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        ResponseEntity<String> result = shopService.purchase(acc.getId(), item.getId());
        assertEquals(400, result.getStatusCodeValue());

        accountService.deleteAccountByID(acc.getId());
        shopRepository.deleteById(item.getId());
    }

    @Test
    @Transactional
    @Order(49)
    public void testPurchase_AlreadyOwnsNonPowerup() {
        Account acc = new Account();
        acc.setUsername("buyer1");
        acc.setPassword("pw");
        acc.setEmail("email1");
        acc.setTotalPoints(500);
        accountRepository.save(acc);

        Shop item = new Shop();
        item.setName("OneTime");
        item.setPrice(50);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        shopService.purchase(acc.getId(), item.getId());
        ResponseEntity<String> result = shopService.purchase(acc.getId(), item.getId());

        assertEquals(409, result.getStatusCodeValue());
        accountService.deleteAccountByID(acc.getId());
        shopRepository.deleteById(item.getId());
    }

    @Test
    @Transactional
    @Order(50)
    public void testPurchase_PowerupStacks() {
        Account acc = new Account();
        acc.setUsername("buyer1");
        acc.setPassword("pw");
        acc.setEmail("email1");
        acc.setTotalPoints(500);
        accountRepository.save(acc);

        Powerup pu = new Powerup();
        pu.setName("SpeedBoost");
        pu.setAffect("Gives you more time");
        pu.setType(PowerupEffects.MINUS_MINUTES);
        powerupRepository.save(pu);

        Shop item = new Shop();
        item.setName("PowerupShop");
        item.setPrice(20);
        item.setItemType(SHOP_ITEM_TYPE.POWERUP);
        item.setPowerup(pu);
        shopRepository.save(item);

        shopService.purchase(acc.getId(), item.getId());
        shopService.purchase(acc.getId(), item.getId());

        UserInventory inv = uiRepo.findByUserIdAndShopItemId(acc.getId(), item.getId()).get();
        assertEquals(2, inv.getQuantity());

        shopRepository.deleteById(item.getId());
        accountRepository.deleteById(acc.getId());
        powerupRepository.deleteById(pu.getId());
    }

    @Test
    @Transactional
    @Order(51)
    public void testPurchase_CreatesTransaction() {
        Account acc = new Account();
        acc.setUsername("buyer1");
        acc.setPassword("pw");
        acc.setEmail("email1");
        acc.setTotalPoints(500);
        accountRepository.save(acc);

        Shop item = new Shop();
        item.setName("TransactionItem");
        item.setPrice(30);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        long countBefore = transactionsRepository.count();
        ResponseEntity a = shopService.purchase(acc.getId(), item.getId());
        assertEquals(200, a.getStatusCodeValue());
        String response = a.getBody().toString();
        String numberOnlyString = response.replaceAll("[^0-9]", "");
        long transactionId = Integer.parseInt(numberOnlyString);
        long countAfter = transactionsRepository.count();

        assertEquals(countBefore + 1, countAfter);
        accountRepository.deleteById(item.getId());
        shopRepository.deleteById(acc.getId());
        transactionsRepository.deleteById(transactionId);
    }

    @Test
    @Transactional
    @Order(52)
    public void testTransaction_GetUserTransactions() {
        Account acc = new Account();
        acc.setUsername("buyer1");
        acc.setPassword("pw");
        acc.setEmail("email1");
        accountRepository.save(acc);

        Shop item = new Shop();
        item.setName("TxItem2");
        item.setPrice(20);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        TransactionDTO dto = new TransactionDTO();
        dto.setUser(acc);
        dto.setShopItem(item);
        dto.setPrice(20);
        dto.setDate(new Date());

        long id = transactionService.addTransaction(dto);

        ResponseEntity<List<Transactions>> result =
                transactionService.getUsersTransactions(acc.getId());

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(1, result.getBody().size());
        accountService.deleteAccountByID(acc.getId());
        shopRepository.deleteById(item.getId());
        transactionsRepository.deleteById(id);
    }

    @Test
    @Transactional
    @Order(53)
    public void testTransaction_GetById() {
        Account acc = new Account();
        acc.setUsername("buyer1");
        acc.setPassword("pw");
        acc.setEmail("email1");
        accountRepository.save(acc);

        Shop item = new Shop();
        item.setName("TxItem");
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        item.setPrice(10);
        shopRepository.save(item);

        TransactionDTO dto = new TransactionDTO();
        dto.setUser(acc);
        dto.setShopItem(item);
        dto.setPrice(10);
        dto.setDate(new Date());

        long id = transactionService.addTransaction(dto);

        ResponseEntity result = transactionService.getTransactionById(id);
        assertEquals(200, result.getStatusCodeValue());
        accountService.deleteAccountByID(acc.getId());
        shopRepository.deleteById(item.getId());
        transactionsRepository.deleteById(id);
    }

    @Test
    @Transactional
    @Order(54)
    public void testEquip_Item() {

        Account acc = new Account();
        acc.setUsername("equipTestUser");
        acc.setPassword("pw");
        acc.setEmail("equip@test.com");
        acc.setTotalPoints(500);
        accountRepository.save(acc);

        Shop item = new Shop();
        item.setName("EquipTestItem");
        item.setDescription("Test decoration");
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        item.setPrice(10);
        shopRepository.save(item);

        shopService.purchase(acc.getId(), item.getId());

        ResponseEntity response = shopService.equip(item.getName(), acc.getId());
        assertEquals(200, response.getStatusCodeValue());

        UserInventory inv = uiRepo
                .findByUserIdAndShopItemId(acc.getId(), item.getId())
                .get();

        assertTrue(inv.isEquipped());


        accountService.deleteAccountByID(acc.getId());
        shopRepository.deleteById(item.getId());
    }

    @Test
    @Transactional
    @Order(55)
    public void testUnequip_Item() {

        Account acc = new Account();
        acc.setUsername("unequipTestUser");
        acc.setPassword("pw");
        acc.setEmail("unequip@test.com");
        acc.setTotalPoints(500);
        accountRepository.save(acc);

        Shop item = new Shop();
        item.setName("UnequipTestItem");
        item.setDescription("Test decoration");
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        item.setPrice(10);
        shopRepository.save(item);

        shopService.purchase(acc.getId(), item.getId());
        shopService.equip(item.getName(), acc.getId());

        ResponseEntity response = shopService.unequip(item.getName(), acc.getId());
        assertEquals(200, response.getStatusCodeValue());

        UserInventory inv = uiRepo
                .findByUserIdAndShopItemId(acc.getId(), item.getId())
                .get();

        assertFalse(inv.isEquipped());

        accountService.deleteAccountByID(acc.getId());
        shopRepository.deleteById(item.getId());
    }

    @Test
    @Transactional
    @Order(56)
    public void testUsePowerup() {

        Account acc = new Account();
        acc.setUsername("powerupUseUser");
        acc.setPassword("pw");
        acc.setEmail("powerup@test.com");
        acc.setTotalPoints(500);
        accountRepository.save(acc);

        Powerup pu = new Powerup();
        pu.setName("UsePowerupPU");
        pu.setAffect("Speed boost");
        pu.setType(PowerupEffects.MINUS_MINUTES);
        powerupRepository.save(pu);

        Shop item = new Shop();
        item.setName("UsePowerupItem");
        item.setPrice(20);
        item.setItemType(SHOP_ITEM_TYPE.POWERUP);
        item.setPowerup(pu);
        shopRepository.save(item);

        shopService.purchase(acc.getId(), item.getId());
        shopService.purchase(acc.getId(), item.getId());

        ResponseEntity response = shopService.userPowerup(item.getName(), acc.getId());
        assertEquals(200, response.getStatusCodeValue());

        UserInventory inv = uiRepo
                .findByUserIdAndShopItemId(acc.getId(), item.getId())
                .get();

        assertEquals(1, inv.getQuantity());

        accountService.deleteAccountByID(acc.getId());
        shopRepository.deleteById(item.getId());
        powerupRepository.deleteById(pu.getId());
    }

    @Test
    @Transactional
    @Order(57)
    public void testGetItem() {

        Shop item = new Shop();
        item.setName("GetItemTest");
        item.setDescription("Test DESC");
        item.setPrice(15);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        ResponseEntity<Shop> result = shopService.getItem("GetItemTest");

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("GetItemTest", result.getBody().getName());

        shopRepository.deleteById(item.getId());
    }

    @Test
    @Transactional
    @Order(58)
    public void testDeleteItem() {

        Shop item = new Shop();
        item.setName("DeleteItemTest");
        item.setDescription("Delete desc");
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        item.setPrice(10);
        shopRepository.save(item);

        ResponseEntity<String> result = shopService.deleteItem("DeleteItemTest");

        assertEquals(200, result.getStatusCodeValue());
        assertFalse(shopRepository.findByName("DeleteItemTest").isPresent());
    }

    @Test
    @Transactional
    @Order(59)
    public void testGetOfType() {

        Shop s1 = new Shop();
        s1.setName("TypeTestItem1");
        s1.setDescription("Test item 1");
        s1.setPrice(5);
        s1.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(s1);

        Shop s2 = new Shop();
        s2.setName("TypeTestItem2");
        s2.setDescription("Test item 2");
        s2.setPrice(10);
        s2.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(s2);

        Shop s3 = new Shop();
        s3.setName("TypeTestItem3");
        s3.setDescription("Test item 3");
        s3.setPrice(7);
        s3.setItemType(SHOP_ITEM_TYPE.PROFILE_CUSTOMIZATION);
        shopRepository.save(s3);

        ResponseEntity<List<Shop>> result =
                shopService.getOfType(SHOP_ITEM_TYPE.DECORATION);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(2, result.getBody().size());

        shopRepository.deleteById(s1.getId());
        shopRepository.deleteById(s2.getId());
        shopRepository.deleteById(s3.getId());
    }

    @Test
    @Transactional
    @Order(60)
    public void testGetItemByName() {

        Shop item = new Shop();
        item.setName("GetItemByNameTest");
        item.setDescription("desc");
        item.setPrice(9);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        ResponseEntity<Shop> result = shopService.getItem("GetItemByNameTest");

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("GetItemByNameTest", result.getBody().getName());

        shopRepository.deleteById(item.getId());
    }

    @Test
    @Transactional
    @Order(61)
    public void testGetItemById() {

        Shop item = new Shop();
        item.setName("GetItemByIdTest");
        item.setDescription("desc");
        item.setPrice(15);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        ResponseEntity<Shop> result = shopService.getItem(item.getId());

        assertEquals(200, result.getStatusCodeValue());
        assertEquals(item.getId(), result.getBody().getId());

        shopRepository.deleteById(item.getId());
    }

    @Test
    @Transactional
    @Order(62)
    public void testDoesExist() {

        Shop item = new Shop();
        item.setName("DoesExistTestItem");
        item.setDescription("desc");
        item.setPrice(20);
        item.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shopRepository.save(item);

        boolean exists = shopService.doesExist("DoesExistTestItem");
        assertTrue(exists);

        boolean doesntExist = shopService.doesExist("NonExistingItem");
        assertFalse(doesntExist);

        shopRepository.deleteById(item.getId());
    }

    @Test
    @Transactional
    @Order(63)
    void testAddItem() {
        Shop shop = new Shop();
        shop.setName("Test Item");
        shop.setDescription("Test Description");
        shop.setItemType(SHOP_ITEM_TYPE.DECORATION);
        shop.setPrice(100);
        shop.setImage("test_image");

        ResponseEntity<Shop> response = shopService.addItem(shop);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Item", response.getBody().getName());

        long itemId = response.getBody().getId();

        shopRepository.deleteById(itemId);
    }

    @Test
    @Transactional
    @Order(64)
    void testGetAllItems() {
        Shop s1 = new Shop();
        s1.setName("Decor1");
        s1.setDescription("Decoration");
        s1.setItemType(SHOP_ITEM_TYPE.DECORATION);
        s1.setPrice(10);
        s1.setImage("img");
        shopRepository.save(s1);

        Shop s2 = new Shop();
        s2.setName("Profile1");
        s2.setDescription("Profile Item");
        s2.setItemType(SHOP_ITEM_TYPE.PROFILE_CUSTOMIZATION);
        s2.setPrice(15);
        s2.setImage("img");
        shopRepository.save(s2);

        Shop s3 = new Shop();
        s3.setName("Other1");
        s3.setDescription("Other");
        s3.setItemType(SHOP_ITEM_TYPE.OTHER);
        s3.setPrice(5);
        s3.setImage("img");
        shopRepository.save(s3);

        ResponseEntity<Map<SHOP_ITEM_TYPE, List<Shop>>> response = shopService.getAllItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Map<SHOP_ITEM_TYPE, List<Shop>> result = response.getBody();

        assertTrue(result.containsKey(SHOP_ITEM_TYPE.DECORATION));
        assertTrue(result.containsKey(SHOP_ITEM_TYPE.PROFILE_CUSTOMIZATION));
        assertTrue(result.containsKey(SHOP_ITEM_TYPE.OTHER));

        shopRepository.deleteById(s1.getId());
        shopRepository.deleteById(s2.getId());
        shopRepository.deleteById(s3.getId());
    }



}

