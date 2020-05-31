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

import com.example.joinchat.Models.LoginResponse;
import com.example.joinchat.Models.ProfileResponse;
import com.example.joinchat.Models.VerifyOtpBody;
import com.example.joinchat.R;
import com.example.joinchat.activities.StartActivity;
import com.example.joinchat.utils.JsonApiHolder;
import com.example.joinchat.utils.RetrofitInstance;
import com.example.joinchat.utils.prefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpFragment extends Fragment {



    private EditText otp_edit_text;
    private JsonApiHolder jsonApiHolder;
    private prefUtils pr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.otp_fragment, container, false);
        Button verify_otp_button = view.findViewById(R.id.verify_otp_button);
        otp_edit_text = view.findViewById(R.id.otp_edit_text);
        jsonApiHolder = RetrofitInstance.getRetrofitInstance(getContext()).create(JsonApiHolder.class);
        pr = new prefUtils(getContext());
        verify_otp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify_otp();
            }
        });

        return view;
    }

    private void verify_otp() {
        String otp = otp_edit_text.getText().toString().trim();

        if(otp.isEmpty()){
            Toast.makeText(getContext(), "Invalid OTP!", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<LoginResponse> call = jsonApiHolder.verifyOtp(SignUpFragment.user_id, new VerifyOtpBody(otp));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    Toast.makeText(getContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                    LoginResponse loginResponse = response.body();
                    pr.createLogin(loginResponse.getToken(), loginResponse.getUserName());
//                    getProfile();
                    Intent intent = new Intent(getContext(), StartActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProfile() {

        Call<ProfileResponse> call = jsonApiHolder.getUserDetails(prefUtils.getAuthToken());
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if(response.isSuccessful()){
                    ProfileResponse profileResponse = response.body();
                    pr.storeProfile(profileResponse.getName());
                }
                else{
                    Toast.makeText(getContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
