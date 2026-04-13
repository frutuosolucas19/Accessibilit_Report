package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.LoginRequest;
import com.example.acessibilit_report.dto.LoginResponse;
import com.example.acessibilit_report.dto.ForgotPasswordRequest;
import com.example.acessibilit_report.dto.ResetPasswordRequest;
import com.example.acessibilit_report.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    @POST("auth/cadastro")
    Call<User> create(@Body User usuario);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/esqueci-senha")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/nova-senha")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    @GET("usuario")
    Call<User> me();

    @GET("usuario/usuarios")
    Call<java.util.List<User>> list();

    @GET("usuario/{id}")
    Call<User> getById(@Path("id") Long id);

    @PUT("usuario/{id}")
    Call<User> update(@Path("id") Long id, @Body User usuario);

    @DELETE("usuario/{id}")
    Call<Void> delete(@Path("id") Long id);
}

