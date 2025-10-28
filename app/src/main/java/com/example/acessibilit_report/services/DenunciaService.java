package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.DenunciaRequest;
import com.example.acessibilit_report.dto.DenunciaResponse;
import com.example.acessibilit_report.model.Denuncia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DenunciaService {

    // Service
    @POST("denuncia")
    Call<DenunciaResponse> criarDenuncia(@Body DenunciaRequest body);

    @GET("denuncia/minhas")
    Call<List<DenunciaResponse>> minhas();

    @GET("denuncia/{id}")
    Call<DenunciaResponse> obter(@Path("id") Long id);
}
