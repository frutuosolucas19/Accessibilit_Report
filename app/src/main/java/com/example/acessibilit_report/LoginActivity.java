package com.example.acessibilit_report;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

    public static final String PREFS_USER = "Preferencia";
    private EditText txtLogin;
    private EditText txtSenha;
    private Button btnEntrar;
    private TextView txvEsqueceuSenha;
    private TextView txvCadastrar;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        txtLogin = (EditText) findViewById(R.id.editTextEmail);
        txtSenha = (EditText) findViewById(R.id.editTextSenha);
        btnEntrar = (Button) findViewById(R.id.buttonLogin);
        txvCadastrar = (TextView) findViewById(R.id.textViewTelaCadastro);

        txvCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, CadastroUsuarioActivity.class);
                startActivity(intent);
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtLogin.getText().toString().trim();
                String senha = txtSenha.getText().toString().trim();

                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginRequest loginRequest = new LoginRequest(email, senha);
                RetrofitInitializer retrofitInitializer = new RetrofitInitializer();
                UsuarioService service = retrofitInitializer.getUsuario();
                Call<LoginResponse> call = service.login(loginRequest);

                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();

                            // Salvar em SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("nome", loginResponse.getNome());
                            editor.putString("email", loginResponse.getEmail());
                            editor.putString("tipoUsuario", loginResponse.getTipoUsuario());
                            editor.apply();

                            Toast.makeText(LoginActivity.this,
                                    "Bem-vindo, " + loginResponse.getNome(),
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Email ou senha inválidos",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this,
                                "Erro ao conectar: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}