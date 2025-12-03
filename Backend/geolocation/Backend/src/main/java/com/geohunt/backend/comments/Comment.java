package com.geohunt.backend.comments;

import com.geohunt.backend.database.Account;
import com.geohunt.backend.database.Challenges;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Account author;

    @ManyToOne
    private Challenges challenge;

    private String text;

    public Comment(Account author, Challenges challenge, String text){
        this.author = author;
        this.challenge = challenge;
        this.text = text;
    }
}
