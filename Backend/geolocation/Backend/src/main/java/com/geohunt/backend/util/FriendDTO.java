package com.geohunt.backend.util;

import com.geohunt.backend.account.Account;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendDTO {
    private long id;
    private String username;
    private String pfp;
    private String email;
    private boolean isAccepted;

    public FriendDTO(Account account, boolean isAccepted) {
        this.id = account.getId();
        this.username = account.getUsername();
        this.pfp = account.getPfp();
        this.email = account.getEmail();
        this.isAccepted = isAccepted;
    }


}
