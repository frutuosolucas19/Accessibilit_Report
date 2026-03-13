package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Person;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PersonService {

    @GET("pessoa")
    Call<Person> me();

    @GET("pessoa/pessoas")
    Call<List<Person>> list();

    @GET("pessoa/{id}")
    Call<Person> select(@Path("id") Long id);

    @POST("pessoa")
    Call<Person> createPost(@Body Person pessoa);

    @PUT("pessoa/{id}")
    Call<Person> update(@Path("id") Long id, @Body Person pessoa);

    @DELETE("pessoa/{id}")
    Call<Void> delete(@Path("id") Long id);
}

