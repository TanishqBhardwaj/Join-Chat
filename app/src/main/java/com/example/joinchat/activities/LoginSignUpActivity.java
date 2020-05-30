package com.example.joinchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.joinchat.R;
import com.example.joinchat.fragments.SignUpFragment;
import com.example.joinchat.fragments.StartFragment;

public class LoginSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_sign_up);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_login_sign_up,
                new SignUpFragment()).commit();
    }
}
