package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id;
    long latitude;
    long longitude;
    String streetviewurl;
    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonBackReference("account-challenges")
    private Account creator;

    @OneToMany(mappedBy="challenge")
    @JsonManagedReference("challenge-submissions")
    private List<Submissions> submissions;

}
