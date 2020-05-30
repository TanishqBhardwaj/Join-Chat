package com.example.joinchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.joinchat.R;
import com.example.joinchat.utils.prefUtils;

public class SplashActivity extends AppCompatActivity {

    prefUtils pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pr = new prefUtils(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){

                if(pr.isLoggedIn()){
                    Intent intent = new Intent(SplashActivity.this , MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(SplashActivity.this , LoginSignUpActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },1000);

    }
}
