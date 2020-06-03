package com.example.joinchat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.joinchat.Models.LoginBody;
import com.example.joinchat.Models.LoginResponse;
import com.example.joinchat.Models.ProfileResponse;
import com.example.joinchat.R;
import com.example.joinchat.activities.StartActivity;
import com.example.joinchat.utils.JsonApiHolder;
import com.example.joinchat.utils.RetrofitInstance;
import com.example.joinchat.utils.prefUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {


    private EditText email_edit_text;
    private EditText password_edit_text;
    private JsonApiHolder jsonApiHolder;
    private prefUtils pr;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        email_edit_text = view.findViewById(R.id.email_edit_text);
        password_edit_text = view.findViewById(R.id.password_login_edit_text);
        Button login_button = view.findViewById(R.id.login_button);
        TextView sign_up_text_view = view.findViewById(R.id.sign_up_text_view);
        jsonApiHolder = RetrofitInstance.getRetrofitInstance(getContext()).create(JsonApiHolder.class);
        login_button.setOnClickListener(v -> login());
        pr = new prefUtils(getContext());
        sign_up_text_view.setOnClickListener(v -> getFragmentManager().
                beginTransaction().replace(R.id.fragment_container_login_sign_up,
                new SignUpFragment()).commit());

        return view;

    }

    private void login() {
        String email = email_edit_text.getText().toString().trim();
        String password = password_edit_text.getText().toString().trim();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(getContext(), "Invalid email or password!", Toast.LENGTH_SHORT).show();
            return;
        }
        Call<LoginResponse> call = jsonApiHolder.login(new LoginBody(email, password));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
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
                Toast.makeText(getContext(), "Error occurred!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
