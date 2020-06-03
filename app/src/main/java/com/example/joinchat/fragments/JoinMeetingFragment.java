package com.example.joinchat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

public class JoinMeetingFragment extends Fragment {

    EditText session_id_edit_text;
    private prefUtils pr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_meeting_fragment, container, false);
        pr = new prefUtils(getContext());

        session_id_edit_text = view.findViewById(R.id.session_id_edit_text);

        Button join_meeting_button = view.findViewById(R.id.join_button);
        join_meeting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                join_meeting();
            }
        });

        return view;
    }

    private void join_meeting() {
        String session = session_id_edit_text.getText().toString().trim();

        if(session.isEmpty()){
            Toast.makeText(getContext(), "Room Name can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        pr.setVideoSession(session);
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra(MainActivity.FLAG, "0");
        startActivity(intent);
    }
}
