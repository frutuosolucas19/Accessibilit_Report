package com.example.acessibilit_report.retrofit;

import android.content.Context;

import com.example.acessibilit_report.BuildConfig;
import com.example.acessibilit_report.network.AuthInterceptor;
import com.example.acessibilit_report.services.ProtectedApiService;
import com.example.acessibilit_report.services.PublicApiService;
import com.example.acessibilit_report.services.ReportService;
import com.example.acessibilit_report.services.UserService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitInitializer {
    private static Retrofit retrofit;
    private static OkHttpClient httpClient;

    public static synchronized Retrofit getInstance(Context ctx){
        if (retrofit == null){
            String baseUrl = BuildConfig.BASE_URL;
            if (!BuildConfig.DEBUG) {
                if (!baseUrl.startsWith("https://")) {
                    throw new IllegalStateException("Release build requires HTTPS base URL.");
                }
            }

            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(BuildConfig.DEBUG
                    ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);

            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(ctx.getApplicationContext()))
                    .addNetworkInterceptor(log)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .client(httpClient)
                    .build();
        }
        return retrofit;
    }

    public static synchronized OkHttpClient getOkHttpClient(Context ctx) {
        if (httpClient == null) getInstance(ctx);
        return httpClient;
    }

    public static UserService getUsuarioService(Context ctx){
        return getInstance(ctx).create(UserService.class);
    }

    public static ReportService getDenunciaService(Context ctx) {
        return getInstance(ctx).create(ReportService.class);
    }

    public static PublicApiService getPublicApiService(Context ctx) {
        return getInstance(ctx).create(PublicApiService.class);
    }

    public static ProtectedApiService getProtectedApiService(Context ctx) {
        return getInstance(ctx).create(ProtectedApiService.class);
    }
}

