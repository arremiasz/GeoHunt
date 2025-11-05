package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notifications {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id;

    private String message;
    private boolean is_read;
    private LocalDate sentAt;

    @ManyToOne
    @JoinColumn(name="account_id")
    @JsonBackReference("account-notifications")
    private Account target;

}
