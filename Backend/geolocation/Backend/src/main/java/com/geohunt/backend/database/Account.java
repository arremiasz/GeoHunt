package com.geohunt.backend.database;

import jakarta.persistence.*;
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

}
