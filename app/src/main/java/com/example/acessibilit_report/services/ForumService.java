package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Forum;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ForumService {

    @GET("forum")
    Call<Forum> forum();

    @GET("forum/foruns")
    Call<List<Forum>> foruns();

    @GET("forum/{id}")
    Call<Forum> getById(@Path("id") Long id);

    @POST("forum")
    Call<Forum> create(@Body Forum forum);

    @PUT("forum/{id}")
    Call<Forum> update(@Path("id") Long id, @Body Forum forum);

    @DELETE("forum/{id}")
    Call<Void> delete(@Path("id") Long id);
}
