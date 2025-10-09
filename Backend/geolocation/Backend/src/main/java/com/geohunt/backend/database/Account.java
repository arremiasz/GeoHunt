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
    @JsonManagedReference("account-challenges")
    private List<Challenges> challenges;

    @OneToMany(mappedBy="submitter")
    @JsonManagedReference("account-submissions")
    private List<Submissions> submissions;



    @OneToMany(mappedBy = "primary")
    @JsonManagedReference("primary-friends")
    private Set<Friends> sentFriendRequests;


    @OneToMany(mappedBy = "target")
    @JsonManagedReference("target-friends")
    private Set<Friends> receivedFriendRequests;


}
