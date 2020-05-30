package com.example.joinchat.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.joinchat.activities.LoginSignUpActivity;

public class prefUtils {

    private static SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private SharedPreferences.Editor profile_editor;
    private Context ctx;

    private static final String PREF_NAME = "login";

    private static final String IS_LOGIN = "isLoggedIn";
    private static final String IS_PROFILE_SAVED = "profileRequired";

    public static final String KEY_TOKEN = "token";


    public prefUtils(Context context){
        ctx = context;
        sp = ctx.getSharedPreferences(PREF_NAME , Context.MODE_PRIVATE);
        editor = sp.edit();
        profile_editor = sp.edit();
    }

    public void createLogin(String token){

        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void storeProfile() {
        profile_editor.putBoolean(IS_PROFILE_SAVED, true);
        profile_editor.commit();
    }

    public boolean isProfileSaved(){
        return sp.getBoolean(IS_PROFILE_SAVED, false);
    }

    public boolean isLoggedIn(){
        return sp.getBoolean(IS_LOGIN, false);
    }

    public static String getToken() {
        return sp.getString(KEY_TOKEN, null);
    }

    public void logoutUser(){

        editor.clear();
        editor.commit();
        profile_editor.clear();
        profile_editor.commit();
        Intent i = new Intent(ctx, LoginSignUpActivity.class);
        ctx.startActivity(i);

    }
}
