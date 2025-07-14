package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.LoginRequest;
import com.example.acessibilit_report.dto.LoginResponse;
import com.example.acessibilit_report.model.Usuario;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsuarioService {

    @POST("usuario/")
    Call<Usuario> createPost(@Body Usuario usuario);

    @POST("{email}")
    Call <Usuario> reposUsuario(@Body Usuario usuario);

    @GET("/usuario/{email}/{senha}")
    Call<String> email(@Path("email") String email, @Path("senha") String senha);

    @Headers("Content-Type: application/json")
    @POST("/usuario/login")
    Call<LoginResponse> login(@Body LoginRequest request);

}
