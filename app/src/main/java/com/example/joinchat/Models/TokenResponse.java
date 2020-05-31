package com.example.joinchat.Models;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {

    @SerializedName("token")
    private String token;

    public TokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
