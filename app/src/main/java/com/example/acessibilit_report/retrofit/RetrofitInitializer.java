package com.example.acessibilit_report.retrofit;



import com.example.acessibilit_report.services.DenunciaService;
import com.example.acessibilit_report.services.UsuarioService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitInitializer {
    private final Retrofit retrofit;

    public RetrofitInitializer() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
    }

    public UsuarioService getUsuario()     {
        return retrofit.create(UsuarioService.class);
    }

    public DenunciaService getDenuncia() { return  retrofit.create(DenunciaService.class);}
}


