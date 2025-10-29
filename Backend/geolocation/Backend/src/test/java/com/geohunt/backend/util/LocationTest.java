package com.geohunt.backend.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @Test
    void distanceKM() {
        Location location1 = new Location(1.0,1.0);
        Location location2 = new Location(1.1,1.1);

        Location location3 = new Location(45,30);
        Location location4 = new Location(46,31);

        Location location5 = new Location(35, 179);
        Location location6 = new Location(35, -179);

        assertEquals(15.724, roundMultiple(location2.distanceKM(location1), 1000));

        assertEquals(135.79, roundMultiple(location3.distanceKM(location4), 100));

        assertEquals(182.17, roundMultiple(location5.distanceKM(location6), 100));
    }

    @Test
    void distanceMiles(){
        Location location1 = new Location(1.0,1.0);
        Location location2 = new Location(2.0,2.0);

        assertEquals(97.7, roundMultiple(location2.distanceMiles(location1), 100));
    }

    double roundMultiple(double toRound, double factor){
        return Math.round(factor * toRound)/factor;
    }
}