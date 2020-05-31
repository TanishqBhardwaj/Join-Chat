package com.example.joinchat.Models;

import com.google.gson.annotations.SerializedName;

public class SignUpBody {

    private String email;
    private String password;
    private String name;

    public SignUpBody(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }


}
