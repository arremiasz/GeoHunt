package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Submissions {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="challenges_id")
    @JsonBackReference("challenge-submissions")
    private Challenges challenge;

    private long latitude;
    private long longtitude;

    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonBackReference("account-submissions")
    private Account submitter;

    private String photourl;
    private LocalDate submissionTime;
    private int reports;
}

