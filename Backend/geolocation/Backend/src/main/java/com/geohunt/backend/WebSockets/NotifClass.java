package com.geohunt.backend.WebSockets;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotifClass {
    public enum importance{
        Low, Medium, High
    }

    private String author;
    private String message;
    private importance importance;

}
