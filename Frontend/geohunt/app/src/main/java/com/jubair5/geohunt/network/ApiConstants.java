package com.jubair5.geohunt.network;

/**
 * A final class to hold all global API constants for the application.
 * @author Alex Remiasz
 */
public final class ApiConstants {




    private ApiConstants() {}

    // Base URL for all API calls
       public static final String BASE_URL = "http://coms-3090-030.class.las.iastate.edu:8080";
    // Alex Mock Server
//    public static final String BASE_URL = "https://6bfe1ae1-ae39-462f-910e-7d53b5da9867.mock.pstmn.io";

    // Nathan Mock Server
//    public static final String BASE_URL = "https://8ce22578-237f-43d8-bd05-9a8c9cc7d1db.mock.pstmn.io";

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
    public static final String POST_SUBMISSION_ENDPOINT = "/geohunt/submission";

    // Friends Endpoints
    public static final String GET_FRIENDS_ENDPOINT= "/friends";
    public static final String GET_SENT_FRIENDS_ENDPOINT= "/friendRequestsSent";
    public static final String GET_Received_FRIENDS_ENDPOINT= "/friendRequestsRecieved";
    public static final String Send_Friend_Request_ENDPOINT= "/friends/add";
    public static final String Accept_Friend_Request_ENDPOINT= "/friends/accept";
    public static final String Reject_Friend_Request_ENDPOINT= "/friends/reject";
    public static final String Remove_FRIEND = "/friends/remove";

    // PowerUp EndPoints
    public static final String GET_POWERUPS_ENDPOINT = "";
    public static final String RESET_THEMES = "";
    public static final String GET_THEMES_ENDPOINT = "";
    public static final String PURCHASE_THEMES = "";

}
