package com.example.joinchat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.joinchat.Models.SessionResponse;
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

    private JsonApiHolder jsonApiHolder;
    private ProgressBar progressBar;
    public static String S_ID = "session";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.start_fragment, container, false);
        jsonApiHolder = RetrofitInstance.getRetrofitInstance(getContext()).create(JsonApiHolder.class);
        progressBar = view.findViewById(R.id.start_fragment_progress_bar);
        progressBar.setVisibility(View.GONE);
        Button join_meeting_button = view.findViewById(R.id.join_meeting_button);
        Button host_meeting_button = view.findViewById(R.id.host_meeting_button);

        join_meeting_button.setOnClickListener(v ->
                getFragmentManager().beginTransaction().replace(R.id.fragment_container_start_activity,
                new JoinMeetingFragment()).addToBackStack(null).commit());

        host_meeting_button.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            join_meeting_button.setEnabled(false);
            host_meeting_button.setEnabled(false);
            getSessionId();
        });
        return view;
    }

    private void getSessionId() {

        retrofit2.Call<SessionResponse> call = jsonApiHolder.getSession("Bearer " + prefUtils.getAuthToken());
        call.enqueue(new retrofit2.Callback<SessionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SessionResponse> call, retrofit2.Response<SessionResponse> response) {
                if(response.isSuccessful()){
                    SessionResponse sessionResponse = response.body();
                    S_ID = sessionResponse.getSessionId();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container_start_activity,
                            new HostMeetingFragment()).addToBackStack(null).commit();
//                    Intent intent = new Intent(getContext(), MainActivity.class);
//                    intent.putExtra(MainActivity.SESSION_ID, sessionId);
//                    intent.putExtra(MainActivity.FLAG, "1");
//                    startActivity(intent);
                    Log.d("SESSION ID: ", String.valueOf(S_ID));
                }
                else{
                    Toast.makeText(getContext(), "Session ID not fetched!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SessionResponse> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
