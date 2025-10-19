package com.example.busfare_splitterv2.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/register")
    Call<UserResponse> register(@Body UserRequest userRequest);
}
