package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Notifications {
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private @Id long id;

    private String message;
    private boolean readStatus = false;
    private LocalDateTime sentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="account_id")
    @JsonBackReference("account-notifications")
    private Account target;

}
