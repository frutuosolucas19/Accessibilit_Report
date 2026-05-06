package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.ReportRequest;
import com.example.acessibilit_report.dto.ReportResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ReportService {

    @GET("denuncias")
    Call<List<ReportResponse>> denuncia();

    @GET("denuncias")
    Call<List<ReportResponse>> denuncias();

    @POST("denuncias")
    Call<ReportResponse> criarDenuncia(@Body ReportRequest body);

    @GET("denuncias/minhas")
    Call<List<ReportResponse>> minhas();

    @GET("denuncias/{id}")
    Call<ReportResponse> obter(@Path("id") Long id);

    @PUT("denuncias/{id}")
    Call<ReportResponse> atualizar(@Path("id") Long id, @Body ReportRequest body);

    @DELETE("denuncias/{id}")
    Call<Void> deletar(@Path("id") Long id);

    @GET("denuncias/{id}/imagens/{imgId}")
    Call<ResponseBody> imagem(@Path("id") Long id, @Path("imgId") Long imgId);

    @DELETE("denuncias/{id}/imagens/{imgId}")
    Call<Void> deletarImagem(@Path("id") Long reportId, @Path("imgId") Long imagemId);
}

