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
    private static final String USER_NAME = "name";
    private static final String KEY_TOKEN = "token";
    private static final String VIDEO_TOKEN = "videoToken";
    private static final String VIDEO_SESSION = "videoSession";


    public prefUtils(Context context){
        ctx = context;
        sp = ctx.getSharedPreferences(PREF_NAME , Context.MODE_PRIVATE);
        editor = sp.edit();
        profile_editor = sp.edit();
    }

    public void createLogin(String token, String name){

        editor.putString(USER_NAME, name);
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public void storeProfile(String name) {
        profile_editor.putBoolean(IS_PROFILE_SAVED, true);
        profile_editor.putString(USER_NAME, name);
        profile_editor.commit();
    }

    public void setVideoSession(String session) {
        editor.putString(VIDEO_SESSION, session);
        editor.commit();
    }

    public void setVideoToken(String token) {
        editor.putString(VIDEO_TOKEN, token);
        editor.commit();
    }

    public String getVideoSession() {
        return sp.getString(VIDEO_SESSION, null);
    }

    public String getVideoToken() {
        return sp.getString(VIDEO_TOKEN, null);
    }

    public boolean isProfileSaved(){
        return sp.getBoolean(IS_PROFILE_SAVED, false);
    }

    public boolean isLoggedIn(){
        return sp.getBoolean(IS_LOGIN, false);
    }

    public String getUserName(){
        return sp.getString(USER_NAME, null);
    }

    public static String getAuthToken() {
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
