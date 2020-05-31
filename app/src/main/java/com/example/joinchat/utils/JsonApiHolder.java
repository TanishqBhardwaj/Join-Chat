package com.example.joinchat.utils;

import com.example.joinchat.Models.LoginBody;
import com.example.joinchat.Models.LoginResponse;
import com.example.joinchat.Models.ProfileResponse;
import com.example.joinchat.Models.SessionResponse;
import com.example.joinchat.Models.SignUpBody;
import com.example.joinchat.Models.SignUpResponse;
import com.example.joinchat.Models.TokenBody;
import com.example.joinchat.Models.TokenResponse;
import com.example.joinchat.Models.VerifyOtpBody;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonApiHolder {

    @POST("user/signup")
    Call<SignUpResponse> signUp(@Body SignUpBody signUpBody);

    @POST("user/login")
    Call<LoginResponse> login(@Body LoginBody loginBody);

    @POST("user/otpVerify/{userId}")
    Call<LoginResponse> verifyOtp(@Path("userId") String user_id, @Body VerifyOtpBody otpBody);

    @GET("user/myDetails")
    Call<ProfileResponse> getUserDetails(@Query("token") String token);

    @GET("user/getSession")
    Call<SessionResponse> getSession(@Header ("Authorization") String token);

    @POST("user/getToken")
    Call<TokenResponse> getVideoToken(@Header("Authorization") String token, @Body TokenBody tokenBody);

}
