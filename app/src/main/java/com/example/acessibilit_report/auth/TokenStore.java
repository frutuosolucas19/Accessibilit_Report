package com.example.acessibilit_report.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenStore {
    private final SharedPreferences prefs;
    public TokenStore(Context ctx) {
        this.prefs = ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
    }
    public void save(String token){ prefs.edit().putString("access_token", token).apply(); }
    public String get(){ return prefs.getString("access_token", null); }
    public void clear(){ prefs.edit().remove("access_token").apply(); }
}
