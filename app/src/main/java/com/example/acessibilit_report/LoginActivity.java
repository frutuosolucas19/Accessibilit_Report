package com.example.acessibilit_report;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.dto.LoginRequest;
import com.example.acessibilit_report.dto.LoginResponse;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.UsuarioService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "user_data";
    public static final String KEY_NOME  = "nome";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_TIPO  = "tipoUsuario";
    public static final String KEY_TOKEN = "access_token";

    private EditText txtLogin;
    private EditText txtSenha;
    private Button btnEntrar;
    private TextView txvCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        txtLogin     = findViewById(R.id.editTextEmail);
        txtSenha     = findViewById(R.id.editTextSenha);
        btnEntrar    = findViewById(R.id.buttonLogin);
        txvCadastrar = findViewById(R.id.textViewTelaCadastro);

        txvCadastrar.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, CadastroUsuarioActivity.class)));

        btnEntrar.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String email = txtLogin.getText().toString().trim().toLowerCase();
        String senha = txtSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEntrar.setEnabled(false);

        LoginRequest body = new LoginRequest(email, senha);
        UsuarioService service = RetrofitInitializer.getUsuarioService(this);

        service.login(body).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> resp) {
                btnEntrar.setEnabled(true);

                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(LoginActivity.this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginResponse lr = resp.body();

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit()
                        .putString(KEY_NOME,  safe(lr.getNome()))
                        .putString(KEY_EMAIL, safe(lr.getEmail()))
                        .putString(KEY_TIPO,  safe(lr.getTipoUsuario()))
                        .putString(KEY_TOKEN, safe(lr.getAccessToken()))
                        .apply();

                Toast.makeText(LoginActivity.this, "Bem-vindo, " + safe(lr.getNome()), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnEntrar.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Erro ao conectar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safe(String s) { return s == null ? "" : s; }
}
