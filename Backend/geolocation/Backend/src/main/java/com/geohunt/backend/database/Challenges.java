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
public class Challenges {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id;
    int latitude;
    int longitude;
    String streetviewurl;
    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonManagedReference
    private Account creator;
    LocalDate creationdate;
}
