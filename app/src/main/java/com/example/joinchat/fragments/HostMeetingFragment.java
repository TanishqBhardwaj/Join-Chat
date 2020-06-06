package com.example.joinchat.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.joinchat.Models.SessionResponse;
import com.example.joinchat.R;
import com.example.joinchat.activities.MainActivity;
import com.example.joinchat.utils.JsonApiHolder;
import com.example.joinchat.utils.RetrofitInstance;
import com.example.joinchat.utils.prefUtils;

public class HostMeetingFragment extends Fragment {

    private EditText room_id_edit_text;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.host_meeting_fragment, container, false);
        room_id_edit_text = view.findViewById(R.id.room_id_edit_text);
        room_id_edit_text.setText(StartFragment.S_ID);
        TextView copy_text_view = view.findViewById(R.id.copy_room_id_text_view);
        copy_text_view.setOnClickListener(v -> copyRoomId());
        Button host_meeting_button = view.findViewById(R.id.host_meeting);
        host_meeting_button.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.putExtra(MainActivity.SESSION_ID, room_id_edit_text.getText().toString().trim());
            intent.putExtra(MainActivity.FLAG, "1");
            startActivity(intent);
        });

        return view;
    }

    private void copyRoomId() {
        String room_id = room_id_edit_text.getText().toString().trim();
        ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("key", room_id);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
    }


}
