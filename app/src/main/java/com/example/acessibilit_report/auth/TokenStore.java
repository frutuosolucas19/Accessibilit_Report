package com.example.acessibilit_report.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenStore {

    private static final String TAG = "TokenStore";
    public static final String PREFS_NAME = "user_secure_prefs";
    public static final String KEY_TOKEN  = "access_token";
    public static final String KEY_NOME   = "nome";
    public static final String KEY_EMAIL  = "email";
    public static final String KEY_TIPO   = "tipoUsuario";

    private final SharedPreferences prefs;

    public TokenStore(Context ctx) {
        SharedPreferences p;
        try {
            MasterKey masterKey = new MasterKey.Builder(ctx.getApplicationContext())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            p = EncryptedSharedPreferences.create(
                    ctx.getApplicationContext(),
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "EncryptedSharedPreferences não disponível, usando SharedPreferences padrão", e);
            p = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        this.prefs = p;
    }

    public void saveSession(String token, String nome, String email, String tipoUsuario) {
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putString(KEY_NOME, nome)
                .putString(KEY_EMAIL, email)
                .putString(KEY_TIPO, tipoUsuario)
                .apply();
    }

    public void save(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String get() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getNome() {
        return prefs.getString(KEY_NOME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public String getTipoUsuario() {
        return prefs.getString(KEY_TIPO, "");
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
