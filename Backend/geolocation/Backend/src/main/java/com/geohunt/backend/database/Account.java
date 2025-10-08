package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "creator")
    @JsonManagedReference
    private List<Challenges> challenges;

}
