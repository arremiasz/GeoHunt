package com.geohunt.backend.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.AccountRepository;
import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GeohuntService {

    @Autowired
    private ChallengesRepository challengesRepository;

    @Autowired
    private AccountService accountService;

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 3959; // miles
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public List<Challenges> getPossibleChallenges(double lat, double lon, double rad) {
        List<Challenges> all = challengesRepository.findAll();
        return new ArrayList<>(
                all.stream()
                        .filter(c -> haversine(lat, lon, c.getLatitude(), c.getLongitude()) <= rad)
                        .toList()
        );
    }

    public Challenges getChallenge(double lat, double lon, double rad) {
        List<Challenges> possible = getPossibleChallenges(lat, lon, rad);

        List<Challenges> generated = generateChallenges(lat, lon, rad, 3);
        challengesRepository.saveAll(generated);
        possible.addAll(generated);

        Random random = new Random();
        Challenges r = possible.get(random.nextInt(possible.size()));
        return r;

    }

    public boolean doChallengesExist(double lat, double lon) {
        String key = "AIzaSyA4cGMdtzfM4Ub-1agmFLqKP5WLWLLwLLg";
        String placesUrl = String.format(
                "https://maps.googleapis.com/maps/api/streetview/metadata?location=LAT=%f,LNG=%f&key=%s",
                lat, lon, key
        );
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(placesUrl))
                .GET()
                .build();
        try{
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            JsonNode results = root.get("status");
            if(results.equals("OK")){
                return true;
            } else {
                return false;
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        return true;
    }

    public List<Challenges> generateChallenges(double lat, double lon, double rad, int count) {
        List<Challenges> generated = new ArrayList<>();
        String apiKey = "AIzaSyA4cGMdtzfM4Ub-1agmFLqKP5WLWLLwLLg";
        ObjectMapper mapper = new ObjectMapper();
        HttpClient client = HttpClient.newHttpClient();

        try {
            // Convert miles to meters for Places API
            int radiusMeters = (int) (rad * 1609.34);

            // Example query: “tourist attractions”, “landmarks”, “parks”, etc.
            String placesUrl = String.format(
                    "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%f,%f&radius=%d,&key=%s",
                    lat, lon, radiusMeters, apiKey
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(placesUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode root = mapper.readTree(response.body());
            JsonNode results = root.get("results");

            if (results == null || !results.isArray() || results.isEmpty()) {
                System.out.println("No landmarks found in the area, falling back to random generation.");
                return fallbackGenerate(lat, lon, rad, count);
            }

            int added = 0;
            for (JsonNode place : results) {
                if (added >= count) break;

                JsonNode location = place.path("geometry").path("location");
                double newLat = location.path("lat").asDouble();
                double newLon = location.path("lng").asDouble();
                String name = place.path("name").asText("Unknown Landmark");

                String streetviewUrl = String.format(
                        "https://maps.googleapis.com/maps/api/streetview?size=600x400&location=%f,%f&key=%s",
                        newLat, newLon, apiKey
                );

                Challenges challenge = new Challenges();
                challenge.setLatitude(newLat);
                challenge.setLongitude(newLon);
                challenge.setStreetviewurl(streetviewUrl);
                challenge.setCreationdate(LocalDate.now());
                challengesRepository.save(challenge);

                generated.add(challenge);
                added++;
            }

            return generated;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error using Places API, falling back to random generation.");
            return fallbackGenerate(lat, lon, rad, count);
        }
    }

    private double[] randomLocation(double lat, double lon, double radiusMiles) {
        double radiusKm = radiusMiles * 1.60934;
        double radiusEarthKm = 6371.0;

        double radiusRadians = radiusKm / radiusEarthKm;
        double u = Math.random();
        double v = Math.random();

        double w = radiusRadians * Math.sqrt(u);
        double t = 2 * Math.PI * v;

        double newLat = Math.asin(Math.sin(Math.toRadians(lat)) * Math.cos(w)
                + Math.cos(Math.toRadians(lat)) * Math.sin(w) * Math.cos(t));
        double newLon = Math.toRadians(lon) + Math.atan2(
                Math.sin(t) * Math.sin(w) * Math.cos(Math.toRadians(lat)),
                Math.cos(w) - Math.sin(Math.toRadians(lat)) * Math.sin(newLat)
        );

        return new double[]{Math.toDegrees(newLat), Math.toDegrees(newLon)};
    }

    public void deleteChallengeByID(long id) {
        challengesRepository.deleteById(id);
    }

    public List<Challenges> fallbackGenerate(double lat, double lon, double rad, int count) {
        List<Challenges> generated = new ArrayList<>();
        String apiKey = "AIzaSyA4cGMdtzfM4Ub-1agmFLqKP5WLWLLwLLg";
        for (int i = 0; i < count; i++) {
            double[] coords = randomLocation(lat, lon, rad);
            double newLat = coords[0];
            double newLon = coords[1];

            String streetviewUrl = String.format(
                    "https://maps.googleapis.com/maps/api/streetview?size=600x400&location=%f,%f&key=%s",
                    newLat, newLon, apiKey
            );

            Challenges challenge = new Challenges();
            challenge.setLatitude(newLat);
            challenge.setLongitude(newLon);
            challenge.setStreetviewurl(streetviewUrl);
            challenge.setCreationdate(LocalDate.now());
            challengesRepository.save(challenge);
            generated.add(challenge);
        }
        return generated;
    }

    public ResponseEntity customChallenge(double lat, double lng, long uid, String url){
        try{
            Challenges challenge = new Challenges();
            Account a = accountService.getAccountById(uid);
            challenge.setLatitude(lat);
            challenge.setLongitude(lng);
            challenge.setStreetviewurl(url);
            challenge.setCreationdate(LocalDate.now());
            challenge.setCreator(a);
            challengesRepository.save(challenge);
            return ResponseEntity.ok(challenge);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    public ResponseEntity getUsersChallenges(long id) {
        try{
            List<Challenges> returnable = challengesRepository.getChallengesByCreator(accountService.getAccountById(id));
            return ResponseEntity.status(HttpStatus.OK).body(returnable);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity deleteUsersChallenges(long uid, long cid){
        Account user;
        try{
            user = accountService.getAccountById(uid);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        List<Challenges> returnable = challengesRepository.getChallengesByCreator(user);
        for(Challenges challenge : returnable){
            if(challenge.getId() == cid){
                challengesRepository.deleteById(challenge.getId());
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Challenge not found.");
    }
}
