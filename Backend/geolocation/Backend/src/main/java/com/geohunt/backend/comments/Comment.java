package com.geohunt.backend.comments;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Challenges;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author Evan Julson
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Account author;

    @ManyToOne
    private Challenges challenge;

    private String comment;

    private LocalDateTime timeStamp;

    public Comment(Account author, Challenges challenge, String comment){
        this.author = author;
        this.challenge = challenge;
        this.comment = comment;
        timeStamp = LocalDateTime.now();
    }
}
