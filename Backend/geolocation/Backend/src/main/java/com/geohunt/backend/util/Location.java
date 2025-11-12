package com.geohunt.backend.util;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {
    private static final double EARTH_RADIUS_KM = 6371; // Constant for the radius of the earth

    private static final double KM_TO_MILES = 0.6213712; // Number of miles in a Kilometer
    private static final double MILES_TO_FEET = 5280; // Number of feet in a mile

    public double latitude; // Latitude of a location, aka degrees north/south
    public double longitude; // Longitude of a location, aka degrees east/west

    public Location(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Finds the distance between two points on a sphere via the Haversine formula: https://en.wikipedia.org/wiki/Haversine_formula
     * May have issues when comparing two points on opposite sides of the globe... need to check.
     * @param other
     * @return double distance in KM
     */
    public double distanceKM(Location other){
        // Variables
        double lat1 = degreesToRadians(this.latitude); // latitude in radians
        double long1 = degreesToRadians(this.longitude); // longitude in radians

        double lat2 = degreesToRadians(other.latitude); // latitude in radians
        double long2 = degreesToRadians(other.longitude); // longitude in radians

        double difLat = lat2 - lat1;
        double difLong = long2 - long1;

        // Calculation
        double step1 = 1 - Math.cos(difLat) + Math.cos(lat1) * Math.cos(lat2) * (1 - Math.cos(difLong));
        double step2 = Math.sqrt(step1/2);
        double distance = 2 * EARTH_RADIUS_KM * Math.asin(step2);

        return distance;
    }

    public double distanceFeet(Location other){
        return KM_TO_MILES * MILES_TO_FEET * distanceKM(other);
    }

    public double distanceMiles(Location other){
        return KM_TO_MILES * distanceKM(other);
    }

    public boolean isAtZero(){
        return longitude == 0 && latitude == 0;
    }

    private double degreesToRadians(double angleDegrees){
        return angleDegrees * (Math.PI/180);
    }



}
