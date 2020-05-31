package com.example.joinchat.Models;

import com.google.gson.annotations.SerializedName;

public class SessionResponse {

    @SerializedName("id")
    private String sessionId;

    public SessionResponse(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
