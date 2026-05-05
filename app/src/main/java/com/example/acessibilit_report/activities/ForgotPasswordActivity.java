package com.example.acessibilit_report.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.dto.ForgotPasswordRequest;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.UserService;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText txtEmail;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        txtEmail = findViewById(R.id.editTextEmailRecuperacao);
        btnEnviar = findViewById(R.id.buttonEnviarTrocaSenha);

        btnEnviar.setOnClickListener(v -> enviarSolicitacao());
    }

    private void enviarSolicitacao() {
        String email = txtEmail.getText().toString().trim().toLowerCase(Locale.ROOT);
        if (email.isEmpty()) {
            Toast.makeText(this, getString(R.string.forgot_email_obrigatorio), Toast.LENGTH_SHORT).show();
            return;
        }

        btnEnviar.setEnabled(false);
        UserService service = RetrofitInitializer.getUsuarioService(this);
        service.forgotPassword(new ForgotPasswordRequest(email)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnEnviar.setEnabled(true);
                if (!response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, getString(R.string.forgot_erro_enviar), Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.forgot_codigo_enviado), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnEnviar.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, getString(R.string.erro_de_rede, t.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
