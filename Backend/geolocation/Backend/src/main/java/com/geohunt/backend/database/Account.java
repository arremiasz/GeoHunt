package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.geohunt.backend.Shop.UserInventory;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.geohunt.backend.powerup.Powerup;
import com.geohunt.backend.comments.Comment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"challenges", "submissions", "sentFriendRequests", "receivedFriendRequests", "notifications", "usersInventory", "powerups"})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Account {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id;
    private String username;
    @Column(columnDefinition = "mediumtext")
    private String pfp;
    @JsonIgnore
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("user")
    private List<UserInventory> usersInventory = new ArrayList<>();


    @OneToMany(mappedBy = "author")
    private List<Comment> comments;

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

    @ManyToMany
    @JoinTable(
            name = "account_powerups",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "powerup_id")
    )
    @JsonIgnoreProperties("accounts")
    private Set<Powerup> powerups = new HashSet<>();

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
