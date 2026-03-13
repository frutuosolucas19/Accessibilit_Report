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

    @POST("usuario/cadastro")
    Call<User> cadastro(@Body User usuario);

    @POST("usuario/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("usuario/esqueci-senha")
    Call<Void> esqueciSenha(@Body ForgotPasswordRequest request);

    @POST("usuario/redefinir-senha")
    Call<Void> redefinirSenha(@Body ResetPasswordRequest request);
}
