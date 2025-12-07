package com.geohunt.backend.powerup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geohunt.backend.Services.AccountService;
import com.geohunt.backend.Services.GeohuntService;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import com.geohunt.backend.util.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.Optional;
import java.util.random.RandomGenerator;

@Service
public class PowerupService {

    @Autowired
    private PowerupRepository repository;

    @Autowired
    private GeohuntService geohuntService;
    @Autowired
    private ChallengesRepository challengesRepository;
    @Autowired
    private AccountService accountService;

    public ResponseEntity add(Powerup powerup) {
        if(repository.findByName(powerup.getName()).isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        repository.save(powerup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity delete(long id) {
        if(repository.findById(id).isPresent()){
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity get(long id) {
        Optional<Powerup> powerup = repository.findById(id);
        if(powerup.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(powerup.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity get(String name) {
        Optional<Powerup> powerup = repository.findByName(name);
        if(powerup.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(powerup.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    public ResponseEntity generate(long chalid, Location loc) {
        Powerup pUp = repository.getRandomPowerup();
        Optional<Challenges> c = challengesRepository.findById(chalid);

        if(c.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Challenges chal = c.get();

        Random random = new Random();

        int min = 30;
        int max = 70;

        int randomNumber = random.nextInt(max - min + 1) + min;

        double t = (randomNumber / 100.0);

        double newLat = loc.getLatitude() + t * (chal.getLatitude() - loc.getLatitude());
        double newLon = loc.getLongitude() + t * (chal.getLongitude() - loc.getLongitude());

        double dx = chal.getLongitude() - loc.getLongitude();
        double dy = chal.getLatitude() - loc.getLatitude();

        double len = Math.sqrt(dy * dy + dx * dx);

        double px = -dy / len;
        double py = dx / len;

        randomNumber = random.nextInt(max - min + 1) + min;

        double latBaseRadians = Math.toRadians(newLat);

        double MetersToLat = randomNumber / 111000;
        double MetersToLon = randomNumber / (111000 * Math.cos(latBaseRadians));

        double sign = random.nextBoolean() ? 1 : -1;

        double lat_powerup = newLat + sign * py * MetersToLat;
        double lon_powerup = newLon + sign * px * MetersToLon;

        RandomGenerationDTO rgd = new RandomGenerationDTO();
        rgd.setLon(lon_powerup);
        rgd.setLat(lat_powerup);
        rgd.setPowerup(pUp);

        return ResponseEntity.status(HttpStatus.CREATED).body(rgd);
    }

    public ResponseEntity deduce(long powerupId, long uid, long chalid, Location loc, Location pup) {
        Optional<Powerup> powerup = repository.findById(powerupId);
        if(powerup.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Powerup not found");
        }
        Account acc;
        try{
            acc = accountService.getAccountById(uid);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Powerup p = powerup.get();
        PowerupEffectDTO ped = new PowerupEffectDTO();
        ped.setPowerup(p);
        ped.setAccount(acc);
        ped.setEffects(p.getType());
        switch(p.getType()){
            case MINUS_MINUTES:
                ped.setLocationName("NULL");
                ped.setTimeDecreaseInSeconds(deduceTimeDeduction(chalid, loc, pup));
                break;

            case MINUS_MORE_MINUTES:
                ped.setLocationName("NULL");
                ped.setTimeDecreaseInSeconds(deduceTimeDeduction(chalid, loc, pup) + 900);
                break;
            case LOCATION_HINT_GENERAL:
                ped.setTimeDecreaseInSeconds(0);
                ped.setLocationName(generalClue(chalid));
                break;
            case LOCATION_HINT_SPECIFIC:
                ped.setTimeDecreaseInSeconds(0);
                ped.setLocationName(specificClue(chalid));
                break;
            case OTHER:
                ped.setLocationName(p.getAffect());
                ped.setTimeDecreaseInSeconds(0);
                break;
        }
        return ResponseEntity.status(HttpStatus.OK).body(ped);
    }

    public int deduceTimeDeduction(long chalid, Location loc, Location pup) {
        Optional<Challenges> c = challengesRepository.findById(chalid);
        if(c.isEmpty()){
            return -1;
        }
        Location endLoc = new Location(c.get().getLatitude(), c.get().getLongitude());

        int baseTime = 20;

        double totalDistance = loc.distanceKM(endLoc) * 1000;

        double offset = perpendicularDistance(loc, endLoc, pup);

        double timeDeduction = baseTime + (totalDistance * 0.1) + (offset*0.01);

        return (int) Math.round(timeDeduction);
    }

    /**Formula from: https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line **/

    public double perpendicularDistance(Location loc, Location end, Location pup) {

        double x1 = loc.lonToMeters();
        double y1 = loc.latToMeters();

        double x2 = end.lonToMeters();
        double y2 = end.latToMeters();

        double xp = pup.lonToMeters();
        double yp = pup.latToMeters();

        double A = xp - x1;
        double B = yp - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = dot / lenSq;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = xp - xx;
        double dy = yp - yy;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public String specificClue(long chalid) {
        Optional<Challenges> c = challengesRepository.findById(chalid);
        if(c.isEmpty()){
            return "Error - No challenge found";
        }

        JsonNode results = APIInteractions(c);

        if (results == null || !results.isArray() || results.isEmpty()) {
            return "Error - no results returned from Google API";
        }

        JsonNode result = results.get(0);

        if (result == null) {
            return "Error - empty result";
        }

        JsonNode components = result.get("address_components");

        String streetName = getAddressComponent(components, "route");

        if (streetName != null) return streetName;

        String neighborhood = getAddressComponent(components, "sublocality");

        if (neighborhood != null) return neighborhood;

        String zip = getAddressComponent(components, "postal_code");

        if (zip != null) return zip;

        return "No street name found";
    }


    public String generalClue(long chalid){
        Optional<Challenges> c = challengesRepository.findById(chalid);
        if(c.isEmpty()){
            return "Error - No challenge found";
        }

        JsonNode results = APIInteractions(c);

        if (results == null || !results.isArray() || results.isEmpty()) {
            return "Error - no results returned from Google API";
        }

        JsonNode result = results.get(0);

        if (result == null) {
            return "Error - empty result";
        }

        JsonNode components = result.get("address_components");

        String locality = getAddressComponent(components, "long_name");
        if ( locality != null) return locality;
        String county = getAddressComponent(components, "administrative_area_level_2");
        if (county != null) return county;
        String state = getAddressComponent(components, "administrative_area_level_1");
        if (state != null) return state;

        return "Error - No locality county or state found.";

    }

    public JsonNode APIInteractions(Optional<Challenges> c) {
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClient.newHttpClient();

        try{
            String apiKey = "AIzaSyA4cGMdtzfM4Ub-1agmFLqKP5WLWLLwLLg";

            String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%,.2f,%,.2f&key=%s",
                    c.get().getLatitude(), c.get().getLongitude(), apiKey);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            JsonNode results = root.get("results");
            return results;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public String getAddressComponent(JsonNode components, String typeToFind) {
        if (components == null || !components.isArray()) {
            return null;
        }

        for (JsonNode comp : components) {
            JsonNode types = comp.get("types");
            if (types == null) continue;

            for (JsonNode t : types) {
                if (t.asText().equals(typeToFind)) {
                    return comp.get("long_name").asText();  // or "short_name"
                }
            }
        }

        return null;
    }
}
