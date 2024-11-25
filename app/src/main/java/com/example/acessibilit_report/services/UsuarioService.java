package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsuarioService {

    @POST("usuario/")
    Call<Usuario> createPost(@Body Usuario usuario);

    @POST("{login}")
    Call <Usuario> reposUsuario(@Body Usuario usuario);

    @GET("/usuario/{login}/{senha}")
    Call<String> login(@Path("login") String login, @Path("senha") String senha);

}
