package com.geohunt.backend.database;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id;
    public String username;
    public String pfp;
    public String password;
    public String email;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

}
