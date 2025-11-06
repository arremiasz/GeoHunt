package com.geohunt.backend.database;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
class FriendKey implements Serializable {
    @Column(name="primary_id")
    private long primaryId;

    @Column(name="target_id")
    private long targetId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendKey)) return false;
        FriendKey key = (FriendKey) o;
        return Objects.equals(primaryId, key.primaryId) &&
                Objects.equals(targetId, key.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryId, targetId);
    }
}
