/**
 * A final class to hold all global API constants for the application.
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.network;

public final class ApiConstants {

    private ApiConstants() {}

    // Base URL for all API calls
//    private static final String BASE_URL = "http://coms-3090-030.class.las.iastate.edu:3306";
    public static final String BASE_URL = "https://8ce22578-237f-43d8-bd05-9a8c9cc7d1db.mock.pstmn.io";

    // Account Endpoints
    public static final String SIGNUP_ENDPOINT = "/signup";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String GET_ACCOUNT_BY_USERNAME_ENDPOINT = "/account/byName";
    public static final String GET_ACCOUNT_BY_ID_ENDPOINT = "/account/byId";
    public static final String DELETE_ACCOUNT_ENDPOINT = "/account/byId";
    public static final String UPDATE_ACCOUNT_ENDPOINT = "/account/update";
    public static final String ADD_LOCAL_ENDPOINT = "/addLocal";
    public static final String LOCATIONS_ENDPOINT = "/locations";

}
