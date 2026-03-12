package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.LoginRequest;
import com.example.acessibilit_report.dto.LoginResponse;
import com.example.acessibilit_report.dto.ForgotPasswordRequest;
import com.example.acessibilit_report.dto.ResetPasswordRequest;
import com.example.acessibilit_report.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

    @POST("usuario")
    Call<User> create(@Body User usuario);

    @POST("usuario/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("usuario/esqueci-senha")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("usuario/redefinir-senha")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

}

