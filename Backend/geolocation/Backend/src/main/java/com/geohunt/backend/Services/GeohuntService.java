package com.geohunt.backend.Services;

import com.geohunt.backend.database.Challenges;
import com.geohunt.backend.database.ChallengesRepository;
import com.geohunt.backend.database.SubmissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class GeohuntService {

    @Autowired
    private ChallengesRepository challengesRepository;

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

        if (possible.size() < 2) {
            List<Challenges> generated = generateChallenges(lat, lon, rad, 3);
            challengesRepository.saveAll(generated);
            possible.addAll(generated);
        }

        Random random = new Random();
        Challenges r = possible.get(random.nextInt(possible.size()));
        return r;
    }

    public List<Challenges> generateChallenges(double lat, double lon, double rad, int count) {
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

            generated.add(challenge);
        }

        return generated;
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
}
