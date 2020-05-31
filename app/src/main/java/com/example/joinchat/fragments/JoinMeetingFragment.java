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

import com.example.joinchat.R;
import com.example.joinchat.activities.MainActivity;

public class JoinMeetingFragment extends Fragment {

    EditText name_edit_text;
    EditText email_edit_text;
    EditText session_id_edit_text;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.join_meeting_fragment, container, false);
        name_edit_text = view.findViewById(R.id.name_edit_text);
        email_edit_text = view.findViewById(R.id.email_edit_text);
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
        String name = name_edit_text.getText().toString().trim();
        String email = email_edit_text.getText().toString().trim();
        String session = session_id_edit_text.getText().toString().trim();

        if(name.isEmpty()){
            Toast.makeText(getContext(), "Name can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(session.isEmpty()){
            Toast.makeText(getContext(), "Session ID can't be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);

    }
}
