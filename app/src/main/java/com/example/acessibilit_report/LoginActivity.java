package com.example.acessibilit_report;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.model.Usuario;
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
    private String email, senha;
    private Context context = this;

    private RetrofitInitializer retrofitInitializer;

    private UsuarioService usuarioService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        txtLogin = (EditText) findViewById(R.id.editTextEmail);
        txtSenha = (EditText) findViewById(R.id.editTextSenha);
        btnEntrar = (Button) findViewById(R.id.buttonCadastro);
        txvCadastrar = (TextView) findViewById(R.id.textViewTelaCadastro);

        //RetrofitInitializer retrofitInitializer = new RetrofitInitializer();
        //usuarioService = retrofitInitializer.getUsuario();

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

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                //Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                //startActivity(intent);
                //retrofitInitializer = new RetrofitInitializer();
                //validaUsuario(txtLogin.getText().toString(), txtSenha.getText().toString());

                //String login = txtLogin.getText().toString();
                //String senha = txtSenha.getText().toString();

                //Usuario usuario = new Usuario();
                //usuario.setLogin(txtLogin.getText().toString());
                //usuario.setSenha(txtSenha.getText().toString());
                //validaUsuario(usuario);
            }
        });
    }
    private void validaUsuario(Usuario usuario) {

        //Pessoa pessoa = new Pessoa(nome, nomeUsuario, email, imagem);

        //Usuario usuario = new Usuario(pessoa, "normal", email, senha);

        Call<String> call = new RetrofitInitializer().getUsuario().login("joao123@gmail.com", "joao123");

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.code() == 200) {
                           // Login bem-sucedido, iniciar a MenuActivity
                          Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                         startActivity(intent);
                    //     finish();
                     } else {
                        // Exibir mensagem de erro
                        Toast.makeText(LoginActivity.this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }
}