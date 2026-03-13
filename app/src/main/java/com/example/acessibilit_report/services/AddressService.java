package com.example.acessibilit_report.services;

import com.example.acessibilit_report.model.Address;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddressService {

    @GET("endereco")
    Call<Address> endereco();

    @GET("endereco/enderecos")
    Call<List<Address>> enderecos();

    @GET("endereco/{id}")
    Call<Address> getById(@Path("id") Long id);

    @POST("endereco")
    Call<Address> create(@Body Address endereco);

    @PUT("endereco/{id}")
    Call<Address> update(@Path("id") Long id, @Body Address endereco);

    @DELETE("endereco/{id}")
    Call<Void> delete(@Path("id") Long id);
}

