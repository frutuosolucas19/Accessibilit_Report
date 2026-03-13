package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Question;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface QuestionService {

    @GET("pergunta")
    Call<Question> pergunta();

    @GET("pergunta/perguntas")
    Call<List<Question>> perguntas();

    @GET("pergunta/{id}")
    Call<Question> getById(@Path("id") Long id);

    @POST("pergunta")
    Call<Question> create(@Body Question pergunta);

    @PUT("pergunta/{id}")
    Call<Question> update(@Path("id") Long id, @Body Question pergunta);

    @DELETE("pergunta/{id}")
    Call<Void> delete(@Path("id") Long id);
}

