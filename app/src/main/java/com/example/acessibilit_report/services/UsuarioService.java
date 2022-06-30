package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UsuarioService {

    @POST("usuario/")
    Call<Usuario> createPost(@Body Usuario usuario);
}
