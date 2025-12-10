package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.geohunt.backend.util.Location;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author evan juslon
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Submissions {

    public static final double DEFAULT_DOUBLE_VALUE = 0.0;
    public static final double MAX_POINTS = 1000;
    public static final double EXP_DECAY_RATE = 1;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name="challenges_id")
    @JsonBackReference("challenges-submissions")
    private Challenges challenge;

    private double latitude;
    private double longitude;

    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonBackReference("account-submissions")
    private Account submitter;

    @Column(columnDefinition = "mediumtext")
    private String photourl;
    private LocalDateTime submissionTime;
    private int reports;

    private boolean hasGeneratedReward;

    @JsonIgnore
    private long longtitude; // TODO: placeholder until fixed in main and mysql server

    public boolean validate(){
        return true;
    }

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
            return submissionLocation.distanceMiles(challengeLocation);
        }
        catch (Exception e){
            return -1;
        }
    }

    public LocalDateTime setSubmissionTimeToNow(){
        submissionTime = LocalDateTime.now();
        return submissionTime;
    }

    public int getSubmissionPoints(){
        double distance = distanceFromChallenge();
        int points = (int)Math.round(MAX_POINTS * Math.exp(-1 * distance * EXP_DECAY_RATE));
        return points;
    }
}

