package com.jubair5.geohunt.network;

/**
 * A final class to hold all global API constants for the application.
 * 
 * @author Alex Remiasz
 */
public final class ApiConstants {

    private ApiConstants() {
    }

    // Base URL for all API calls
    public static final String BASE_URL = "http://coms-3090-030.class.las.iastate.edu:8080";
    // public static final String BASE_URL = "http://10.0.2.2:3000";

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

    // Shop Endpoints
    public static final String GET_POINTS_ENDPOINT = "/points";
    public static final String PURCHASE_ITEM_ENDPOINT = "/shop/purchase";
    public static final String GET_SHOP_ITEMS_ENDPOINT = "/shop/all";
    public static final String GET_SHOP_TRANSACTIONS_ENDPOINT = "/shop/transactions/by-user";

    // Statistics Endpoints
    public static final String GET_SUBMISSIONS_ENDPOINT = "/account/{uid}/submissions";
    public static final String GET_FRIENDS_ENDPOINT = "/friends";
    public static final String GET_COMMENTS_ENDPOINT = "/account/{uid}/comments";

}
