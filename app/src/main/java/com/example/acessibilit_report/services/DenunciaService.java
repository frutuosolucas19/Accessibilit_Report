package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Denuncia;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DenunciaService {

    @POST("denuncia/")
    Call<Denuncia> createPost(@Body Denuncia denuncia);
}
