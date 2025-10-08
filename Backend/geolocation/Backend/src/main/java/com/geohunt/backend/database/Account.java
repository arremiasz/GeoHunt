package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy="submitter")
    @JsonManagedReference
    private List<Submissions> submissions;


    @OneToMany(mappedBy = "primary")
    private Set<Friends> sentFriendRequests;


    @OneToMany(mappedBy = "target")
    private Set<Friends> receivedFriendRequests;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

}
