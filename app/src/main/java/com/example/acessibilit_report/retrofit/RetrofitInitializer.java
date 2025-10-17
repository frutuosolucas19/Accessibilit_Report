package com.example.acessibilit_report.retrofit;

import android.content.Context;

import com.example.acessibilit_report.network.AuthInterceptor;
import com.example.acessibilit_report.services.UsuarioService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitInitializer {
    private static Retrofit retrofit;

    public static Retrofit getInstance(Context ctx){
        if (retrofit == null){
            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(ctx))   // <-- aqui
                    .addNetworkInterceptor(log)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/") // emulador -> host
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static UsuarioService getUsuarioService(Context ctx){
        return getInstance(ctx).create(UsuarioService.class);
    }
}
