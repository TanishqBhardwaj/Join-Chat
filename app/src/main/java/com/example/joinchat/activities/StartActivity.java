package com.example.joinchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.joinchat.R;
import com.example.joinchat.fragments.StartFragment;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_start_activity,
                new StartFragment()).commit();
    }
}
