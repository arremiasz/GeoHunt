package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.geohunt.backend.data.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Submissions {

    public static final double DEFAULT_DOUBLE_VALUE = 0.0;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="challenges_id")
    @JsonBackReference("challenge-submissions")
    private Challenges challenge;

    private double latitude;
    private double longitude;

    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonBackReference("account-submissions")
    private Account submitter;

    private String photourl;
    private LocalDateTime submissionTime;
    private int reports;

    private long longtitude; // placeholder until fixed in main and mysql server

    public void updateValues(Submissions other){
        // Cannot change id
        if(other.getChallenge() != null){
            this.challenge = other.getChallenge();
        }

        if(other.getSubmitter() != null){
            this.submitter = other.getSubmitter();
        }

        if(other.getLatitude() != DEFAULT_DOUBLE_VALUE){
            this.latitude = other.getLatitude();
        }

        if(other.getLongitude() != DEFAULT_DOUBLE_VALUE){
            this.longitude = other.getLongitude();
        }

        if(other.getPhotourl() != null && !other.getPhotourl().isBlank()){
            this.photourl = other.getPhotourl();
        }

        if(other.getReports() != 0){
            this.reports = other.getReports();
        }
    }

    /**
     *
     * @return double distance
     */
    public double distanceFromChallenge(){
        try{
            Location submissionLocation = new Location(this.latitude, this.longitude);
            Location challengeLocation = new Location(challenge.getLatitude(), challenge.getLongitude());
            return submissionLocation.distanceKM(challengeLocation);
        }
        catch (Exception e){
            return -1;
        }

    }
}

