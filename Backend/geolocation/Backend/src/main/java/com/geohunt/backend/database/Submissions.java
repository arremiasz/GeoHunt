package com.geohunt.backend.database;

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
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id;
    @ManyToOne
    @JoinColumn(name="challenges_id")
    @JsonManagedReference
    private Challenges challenge;
    private int latitude;
    private int longtitude;
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account submitter;
    private String photourl;
    private LocalDate submissionTime;
    private int reports;
}
