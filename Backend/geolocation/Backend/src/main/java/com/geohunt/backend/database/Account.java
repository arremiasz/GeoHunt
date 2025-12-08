package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"challenges", "submissions", "sentFriendRequests", "receivedFriendRequests", "notifications"})
public class Account {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id;
    private String username;
    @Column(columnDefinition = "mediumtext")
    private String pfp;
    private String password;
    private String email;
    private long totalPoints;

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

    public void incrementPoints(long value){
        totalPoints += value;
    }

    public boolean chargePoints(long value){
        if(value > totalPoints){
            return false;
        }
        totalPoints -= value;
        return true;
    }

}
