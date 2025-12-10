package com.geohunt.backend.database;

import lombok.Getter;
import lombok.Setter;

/**
 * @author evan juslon
 */
@Setter
@Getter
public class LogInInfo {

    String username;
    String password;

    public LogInInfo(String username, String password){
        this.username = username;
        this.password = password;
    }
}
