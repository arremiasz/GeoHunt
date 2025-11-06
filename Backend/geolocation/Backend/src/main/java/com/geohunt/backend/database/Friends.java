package com.geohunt.backend.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Friends {

    @EmbeddedId
    private FriendKey id;

    @ManyToOne
    @MapsId("primaryId")
    @JoinColumn(name = "primary_id")
    @JsonBackReference("primary-friends")
    private Account primary;

    @ManyToOne
    @MapsId("targetId")
    @JoinColumn(name = "target_id")
    @JsonBackReference("target-friends")
    private Account target;

    @Column(nullable = false)
    private boolean isAccepted;
}
