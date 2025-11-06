package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
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
    @Column(unique = true)
    private String streetviewurl;
    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonIgnore
    private Account creator;
    LocalDate creationdate;
    @OneToMany(mappedBy="challenge")
    @JsonManagedReference("challenges-submissions")
    private List<Submissions> submissions;
}
