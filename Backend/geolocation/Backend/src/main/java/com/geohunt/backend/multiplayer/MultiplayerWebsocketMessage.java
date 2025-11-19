package com.geohunt.backend.multiplayer;

import com.geohunt.backend.database.Account;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
class MultiplayerWebsocketMessage {
    List<Account> recipients = new ArrayList<>();
    String message;

    public MultiplayerWebsocketMessage(Account recipient, String message){
        this.recipients = new ArrayList<>();
        this.recipients.add(recipient);
        this.message = message;
    }

    public MultiplayerWebsocketMessage(List<Account> recipients, String message){
        this.recipients = recipients;
        this.message = message;
    }

    public void addRecipient(Account recipient){
        recipients.add(recipient);
    }
}
