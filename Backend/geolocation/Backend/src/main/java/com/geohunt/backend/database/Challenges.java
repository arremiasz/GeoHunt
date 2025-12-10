package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.*;
import com.geohunt.backend.comments.Comment;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Challenges {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Id long id;
    double latitude;
    double longitude;

    @Column(unique = true, columnDefinition = "MEDIUMTEXT")
    private String streetviewurl;

    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonIgnore
    private Account creator;

    LocalDate creationdate;

    @OneToMany(mappedBy="challenge")
    @JsonManagedReference("challenges-submissions")
    private List<Submissions> submissions;

    @OneToMany(mappedBy = "challenge")
    @JsonIgnore
    private List<Comment> comments;

    private List<Integer> challengeRatings = new ArrayList<>();

    public void addRating(int rating){
        if(challengeRatings == null){
            challengeRatings = new ArrayList<>();
        }
        challengeRatings.add(rating);

    }
}
