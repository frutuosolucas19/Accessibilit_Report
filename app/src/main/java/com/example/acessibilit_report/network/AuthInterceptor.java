// com/example/acessibilit_report/network/AuthInterceptor.java
package com.example.acessibilit_report.network;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SharedPreferences prefs;

    public AuthInterceptor(Context ctx) {
        this.prefs = ctx.getSharedPreferences("user_data", Context.MODE_PRIVATE);
    }

    @Override public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String path = req.url().encodedPath();

        boolean isPublic =
                path.equals("/usuario") && req.method().equals("POST")
                        || path.equals("/usuario/login");

        String token = prefs.getString("access_token", null);
        if (!isPublic && token != null && !token.isEmpty()) {
            req = req.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
        }
        return chain.proceed(req);
    }
}
