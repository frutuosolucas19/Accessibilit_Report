package com.example.acessibilit_report;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.acessibilit_report.model.Usuario;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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
    private String email, senha;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        txtLogin = (EditText) findViewById(R.id.editTextEmail);
        txtSenha = (EditText) findViewById(R.id.editTextSenha);
        btnEntrar = (Button) findViewById(R.id.buttonCadastro);
        txvCadastrar = (TextView) findViewById(R.id.textViewTelaCadastro);

        txvCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validaUsuario(txtLogin.getText().toString(), txtSenha.getText().toString());
            }
        });
    }

    public void validaUsuario(final String login, final String senha) {


        Usuario usuario = new Usuario(login);
        Call<Usuario> call = new RetrofitInitializer().autenticaUsuario().reposUsuario(usuario);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                Usuario acesso = response.body();
                if (acesso.getLogin().equals(login) && acesso.getSenha().equals(senha)) {
                    Toast.makeText(context, "Login Realizado com Sucesso!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Falha no Login!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                Toast.makeText(context, "Erro ao estabelecer conexão", Toast.LENGTH_LONG).show();
            }
        });
    }

}