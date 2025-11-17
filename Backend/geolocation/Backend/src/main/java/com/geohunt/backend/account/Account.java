package com.geohunt.backend.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.geohunt.backend.game.Challenges;
import com.geohunt.backend.friends.Friends;
import com.geohunt.backend.notifications.Notifications;
import com.geohunt.backend.game.Submissions;
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
@JsonIgnoreProperties({"challenges", "submissions", "sentFriendRequests", "receivedFriendRequests", "notifications"})
public class Account {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id;
    private String username;
    private String pfp;
    private String password;
    private String email;

    @OneToMany(mappedBy = "creator")
    @JsonManagedReference("account-challenges")
    private List<Challenges> challenges;

    @OneToMany(mappedBy="submitter")
    @JsonManagedReference("account-submissions")
    private List<Submissions> submissions;


    @OneToMany(mappedBy = "primary")
    @JsonManagedReference("primary-friends")
    private Set<Friends> sentFriendRequests;

    @OneToMany(mappedBy="target")
    @JsonManagedReference("account-notifications")
    private List<Notifications> notifications;


    @OneToMany(mappedBy = "target")
    @JsonManagedReference("target-friends")
    private Set<Friends> receivedFriendRequests;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

}
