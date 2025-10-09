/**
 * A final class to hold all global API constants for the application.
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.network;

public final class ApiConstants {

    private ApiConstants() {}

    // Base URL for all API calls
    public static final String BASE_URL = "http://coms-3090-030.class.las.iastate.edu:3306";
//    public static final String BASE_URL = "https://6bfe1ae1-ae39-462f-910e-7d53b5da9867.mock.pstmn.io";

    // Account Endpoints
    public static final String SIGNUP_ENDPOINT = "/signup";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String GET_ACCOUNT_BY_USERNAME_ENDPOINT = "/account/byName";
    public static final String GET_ACCOUNT_BY_ID_ENDPOINT = "/account/byId";
    public static final String DELETE_ACCOUNT_ENDPOINT = "/account/byId";
    public static final String UPDATE_ACCOUNT_ENDPOINT = "/account/update";
}
