/**
 * A final class to hold all global API constants for the application.
 * @author Alex Remiasz
 */
package com.jubair5.geohunt.network;

public final class ApiConstants {

    private ApiConstants() {}

    // Base URL for all API calls
    public static final String BASE_URL = "https://6bfe1ae1-ae39-462f-910e-7d53b5da9867.mock.pstmn.io";

    // Account Endpoints
    public static final String SIGNUP_ENDPOINT = "/signup";
    public static final String DELETE_ACCOUNT_ENDPOINT = "/account/byId";
}
