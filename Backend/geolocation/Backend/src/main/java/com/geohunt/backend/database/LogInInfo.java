package com.geohunt.backend.database;

import lombok.Getter;
import lombok.Setter;

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
