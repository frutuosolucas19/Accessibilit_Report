package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.LoginRequest;
import com.example.acessibilit_report.dto.LoginResponse;
import com.example.acessibilit_report.model.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService {

    @POST("usuario")
    Call<Usuario> create(@Body Usuario usuario);

    @POST("usuario/login")
    Call<LoginResponse> login(@Body LoginRequest request);

}
