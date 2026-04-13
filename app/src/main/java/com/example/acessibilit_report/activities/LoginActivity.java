package com.example.acessibilit_report.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.auth.TokenStore;
import com.example.acessibilit_report.dto.LoginRequest;
import com.example.acessibilit_report.dto.LoginResponse;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.UserService;

import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText txtLogin;
    private EditText txtSenha;
    private Button btnEntrar;
    private TextView txvCadastrar;
    private TextView txvEsqueceuSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        txtLogin = findViewById(R.id.editTextEmail);
        txtSenha = findViewById(R.id.editTextSenha);
        btnEntrar = findViewById(R.id.buttonLogin);
        txvCadastrar = findViewById(R.id.textViewTelaCadastro);
        txvEsqueceuSenha = findViewById(R.id.textViewEsqueceuSenha);

        txvCadastrar.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, UserRegistrationActivity.class)));
        txvEsqueceuSenha.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        setupPasswordToggle();
        btnEntrar.setOnClickListener(v -> doLogin());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordToggle() {
        txtSenha.setOnTouchListener((v, event) -> {
            if (event.getAction() != MotionEvent.ACTION_UP) return false;
            if (txtSenha.getCompoundDrawablesRelative()[2] == null) return false;

            int drawableWidth = txtSenha.getCompoundDrawablesRelative()[2].getIntrinsicWidth();
            int touchAreaStart = txtSenha.getWidth() - txtSenha.getPaddingEnd() - drawableWidth;
            if (event.getX() < touchAreaStart) return false;

            boolean isHidden = txtSenha.getTransformationMethod() instanceof PasswordTransformationMethod;
            txtSenha.setTransformationMethod(
                    isHidden
                            ? HideReturnsTransformationMethod.getInstance()
                            : PasswordTransformationMethod.getInstance()
            );
            txtSenha.setSelection(txtSenha.getText().length());
            v.performClick();
            return true;
        });
    }

    private void doLogin() {
        String email = txtLogin.getText().toString().trim().toLowerCase(Locale.ROOT);
        String senha = txtSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEntrar.setEnabled(false);

        LoginRequest body = new LoginRequest(email, senha);
        UserService service = RetrofitInitializer.getUsuarioService(this);

        service.login(body).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> resp) {
                btnEntrar.setEnabled(true);

                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(LoginActivity.this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginResponse lr = resp.body();

                new TokenStore(LoginActivity.this).saveSession(
                        safe(lr.getAccessToken()),
                        safe(lr.getNome()),
                        safe(lr.getEmail()),
                        safe(lr.getTipoUsuario())
                );

                Toast.makeText(LoginActivity.this, "Bem-vindo, " + safe(lr.getNome()), Toast.LENGTH_LONG).show();

                String tipo = lr.getTipoUsuario();
                if ("CLIENTE".equalsIgnoreCase(tipo)) {
                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                    finish();
                } else if ("MEDIADOR".equalsIgnoreCase(tipo)) {
                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Tipo de conta desconhecido. Contate o suporte.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnEntrar.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Erro ao conectar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
