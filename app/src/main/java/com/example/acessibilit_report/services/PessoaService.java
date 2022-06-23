package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Pessoa;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PessoaService {

    @POST("pessoa")
    Call<Pessoa> createPost(@Body Pessoa pessoa);

}
