package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.ForgotPasswordRequest;
import com.example.acessibilit_report.dto.LoginRequest;
import com.example.acessibilit_report.dto.LoginResponse;
import com.example.acessibilit_report.dto.ResetPasswordRequest;
import com.example.acessibilit_report.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PublicApiService {

    @POST("auth/cadastro")
    Call<User> cadastro(@Body User usuario);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/esqueci-senha")
    Call<Void> esqueciSenha(@Body ForgotPasswordRequest request);

    @POST("auth/nova-senha")
    Call<Void> redefinirSenha(@Body ResetPasswordRequest request);
}
