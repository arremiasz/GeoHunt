/**
 * A final class to hold all global API constants for the application.
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.network;

public final class ApiConstants {

    private ApiConstants() {}

    // Base URL for all API calls
//    public static final String BASE_URL = "http://coms-3090-030.class.las.iastate.edu:8080";
    public static final String BASE_URL = "https://6bfe1ae1-ae39-462f-910e-7d53b5da9867.mock.pstmn.io";
//    public static final String BASE_URL = "http://10.0.2.2:8080";

    // WebSocket URL
//    public static final String WEBSOCKET_URL = "ws://coms-3090-030.class.las.iastate.edu:8080/game";
    public static final String WEBSOCKET_URL = "ws://10.0.2.2:8080";

    // Account Endpoints
    public static final String SIGNUP_ENDPOINT = "/signup";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String GET_ACCOUNT_BY_USERNAME_ENDPOINT = "/account/byName";
    public static final String GET_ACCOUNT_BY_ID_ENDPOINT = "/account/byId";
    public static final String DELETE_ACCOUNT_ENDPOINT = "/account/byId";
    public static final String UPDATE_ACCOUNT_ENDPOINT = "/account/update";

    // Place Endpoints
    public static final String SUBMIT_PLACE_ENDPOINT = "/geohunt/customChallenge";
    public static final String GET_SUBMITTED_PLACES_ENDPOINT = "/geohunt";
    public static final String DEL_SUBMITTED_PLACE_ENDPOINT = "/geohunt/mySubmissions";

    // Location Endpoints
    public static final String GET_GENERATED_LOCATIONS_ENDPOINT = "/geohunt/getLocation";
    public static final String GET_CHALLENGE_BY_ID_ENDPOINT = "/geohunt/getChallengeByID";
    public static final String POST_SUBMISSION_ENDPOINT = "/geohunt/submission";


}
