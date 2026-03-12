package com.example.acessibilit_report.services;

import com.example.acessibilit_report.dto.ReportRequest;
import com.example.acessibilit_report.dto.ReportResponse;
import com.example.acessibilit_report.model.Report;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReportService {

    // Service
    @POST("denuncia")
    Call<ReportResponse> criarDenuncia(@Body ReportRequest body);

    @GET("denuncia/minhas")
    Call<List<ReportResponse>> minhas();

    @GET("denuncia/{id}")
    Call<ReportResponse> obter(@Path("id") Long id);
}

