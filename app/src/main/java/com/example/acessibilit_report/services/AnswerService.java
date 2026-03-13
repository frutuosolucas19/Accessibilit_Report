package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Answer;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AnswerService {

    @GET("resposta")
    Call<Answer> resposta();

    @GET("resposta/respostas")
    Call<List<Answer>> respostas();

    @GET("resposta/{id}")
    Call<Answer> getById(@Path("id") Long id);

    @POST("resposta")
    Call<Answer> create(@Body Answer resposta);

    @PUT("resposta/{id}")
    Call<Answer> update(@Path("id") Long id, @Body Answer resposta);

    @DELETE("resposta/{id}")
    Call<Void> delete(@Path("id") Long id);
}

