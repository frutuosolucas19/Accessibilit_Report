// com/example/acessibilit_report/network/AuthInterceptor.java
package com.example.acessibilit_report.network;

import android.content.Context;

import com.example.acessibilit_report.auth.TokenStore;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenStore tokenStore;

    public AuthInterceptor(Context ctx) {
        this.tokenStore = new TokenStore(ctx);
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String path = req.url().encodedPath();

        boolean isPublic =
                (path.equals("/auth/cadastro") && req.method().equals("POST"))
                        || path.equals("/auth/login")
                        || path.equals("/auth/esqueci-senha")
                        || path.equals("/auth/nova-senha");

        String token = tokenStore.get();
        if (!isPublic && token != null && !token.isEmpty()) {
            req = req.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
        }
        return chain.proceed(req);
    }
}
