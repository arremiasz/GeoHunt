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
//    public static final String BASE_URL = "http://10.0.2.2:3000";
    // Nathan Mock Server
    public static final String NATHAN_MOCK = "https://8ce22578-237f-43d8-bd05-9a8c9cc7d1db.mock.pstmn.io";
//  public static final String BASE_URL = "https://8ce22578-237f-43d8-bd05-9a8c9cc7d1db.mock.pstmn.io";

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
    public static final String GET_CHALLENGE_BY_ID_ENDPOINT = "/geohunt/getChallengeByID";
    public static final String RATE_CHALLENGE_ENDPOINT = "/geohunt/rate";
    public static final String GET_CHALLENGE_COMMENTS_ENDPOINT = "/challenges/";

    // Shop Endpoints
    public static final String GET_POINTS_ENDPOINT = "/points";
    public static final String PURCHASE_ITEM_ENDPOINT = "/shop/purchase";
    public static final String GET_SHOP_ITEMS_ENDPOINT = "/shop/all";
    public static final String GET_SHOP_TRANSACTIONS_ENDPOINT = "/shop/transactions/by-user";

    // Statistics Endpoints
    public static final String GET_SUBMISSIONS_ENDPOINT = "/account/{uid}/submissions";
    public static final String GET_USER_COMMENTS_ENDPOINT = "/account/{uid}/comments";

    // Friends Endpoints
    public static final String GET_FRIENDS_ENDPOINT= "/friends";
    public static final String GET_SENT_FRIENDS_ENDPOINT= "/friendRequestsSent";
    public static final String GET_Received_FRIENDS_ENDPOINT= "/friendRequestsRecieved";
    public static final String Send_Friend_Request_ENDPOINT= "/friends/add";
    public static final String Accept_Friend_Request_ENDPOINT= "/friends/accept";
    public static final String Reject_Friend_Request_ENDPOINT= "/friends/reject";
    public static final String Remove_FRIEND = "/friends/remove";

    // PowerUp EndPoints
    public static final String GET_POWERUPS_ENDPOINT = "powerup/user";
    public static final String GET_REWARDS = "/rewards/";
    public static final String RESET_THEMES = "";
    public static final String GET_THEMES_ENDPOINT = "";
    public static final String PURCHASE_THEMES = "";

}
