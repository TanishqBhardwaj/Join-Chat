package com.example.joinchat.Models;

import com.google.gson.annotations.SerializedName;

public class TokenBody {

    @SerializedName("sessionName")
    private String sessionId;

    public TokenBody(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
