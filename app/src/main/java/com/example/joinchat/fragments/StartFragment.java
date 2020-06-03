package com.example.joinchat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.joinchat.Models.TokenBody;
import com.example.joinchat.Models.TokenResponse;
import com.example.joinchat.R;
import com.example.joinchat.activities.MainActivity;
import com.example.joinchat.utils.JsonApiHolder;
import com.example.joinchat.utils.RetrofitInstance;
import com.example.joinchat.utils.prefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container, false);


        Button join_meeting_button = view.findViewById(R.id.join_meeting_button);
        Button host_meeting_button = view.findViewById(R.id.host_meeting_button);

        join_meeting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container_start_activity,
                        new JoinMeetingFragment()).commit();
            }
        });

        host_meeting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra(MainActivity.FLAG, "1");
                startActivity(intent);
            }
        });
        return view;
    }


}
