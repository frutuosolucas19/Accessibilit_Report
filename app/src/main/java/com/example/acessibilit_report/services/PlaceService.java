package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Person;
import com.example.acessibilit_report.model.Place;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PlaceService {

    @GET("local")
    Call<Place> local();

    @GET("local/pessoas")
    Call<List<Person>> pessoasRelacionadas();

    @GET("local/locais")
    Call<List<Place>> locais();

    @GET("local/{id}")
    Call<Place> getById(@Path("id") Long id);

    @POST("local")
    Call<Place> create(@Body Place local);

    @PUT("local/{id}")
    Call<Place> update(@Path("id") Long id, @Body Place local);

    @DELETE("local/{id}")
    Call<Void> delete(@Path("id") Long id);
}

