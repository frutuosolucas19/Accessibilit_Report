package com.example.acessibilit_report.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenStore {
    public static final String PREFS_NAME = "user_data";
    public static final String KEY_TOKEN = "access_token";

    private final SharedPreferences prefs;

    public TokenStore(Context ctx) {
        this.prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void save(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String get() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void clear() {
        prefs.edit().remove(KEY_TOKEN).apply();
    }
}
