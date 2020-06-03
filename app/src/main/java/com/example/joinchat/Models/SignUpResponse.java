package com.example.joinchat.Models;

import com.google.gson.annotations.SerializedName;

public class SignUpResponse {

    @SerializedName("userId")
    private String user_id;

    private String message;

    public SignUpResponse(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
