package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.GeoLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LocationService {

    @GET("localizacao")
    Call<GeoLocation> localizacao();

    @GET("localizacao/localizacoes")
    Call<List<GeoLocation>> localizacoes();

    @GET("localizacao/{id}")
    Call<GeoLocation> getById(@Path("id") Long id);

    @POST("localizacao")
    Call<GeoLocation> create(@Body GeoLocation localizacao);

    @PUT("localizacao/{id}")
    Call<GeoLocation> update(@Path("id") Long id, @Body GeoLocation localizacao);

    @DELETE("localizacao/{id}")
    Call<Void> delete(@Path("id") Long id);
}


