package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Person;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PersonService {

    @GET("pessoa/{id}")
    Call<Person> select(@Path("id") int id);

    @POST("pessoa/")
    Call<Person> createPost(@Body Person pessoa);

}

