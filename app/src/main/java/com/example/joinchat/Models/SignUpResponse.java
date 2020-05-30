package com.example.joinchat.Models;

import com.google.gson.annotations.SerializedName;

public class SignUpResponse {

    @SerializedName("userId")
    private String user_id;

    public SignUpResponse(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }
}
