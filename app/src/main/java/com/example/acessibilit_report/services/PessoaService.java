package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Pessoa;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PessoaService {

    @GET("pessoa/{id}")
    Call<Pessoa> select(@Path("id") int id);

    @POST("pessoa/")
    Call<Pessoa> createPost(@Body Pessoa pessoa);

}
