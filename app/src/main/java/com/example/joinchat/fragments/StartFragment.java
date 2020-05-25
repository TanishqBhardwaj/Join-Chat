package com.example.joinchat.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.joinchat.R;

public class StartFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container, false);
        Button join_meeting_button = view.findViewById(R.id.join_meeting_button);
        Button host_meeting_button = view.findViewById(R.id.host_meeting_button);

        join_meeting_button.setOnClickListener(v -> getFragmentManager().
                beginTransaction().replace(R.id.fragment_container_start_activity,
                new JoinMeetingFragment()).commit());
        return view;
    }
}
