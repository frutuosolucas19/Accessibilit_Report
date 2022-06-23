package com.example.acessibilit_report;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.model.Pessoa;
import com.example.acessibilit_report.services.PessoaService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CadastroActivity extends AppCompatActivity {

    private EditText txtNome;
    private EditText txtUsuario;
    private EditText txtEmail;
    private EditText txtSenha;
    private EditText txtConfirmaSenha;
    private Button btnCadastro;
    private TextView txvEsqueceuSenha;
    private TextView txvCadastrar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().hide();

        txtNome = (EditText) findViewById(R.id.editTextNome);
        txtUsuario = (EditText) findViewById(R.id.editTextUsuario);
        txtEmail = (EditText) findViewById(R.id.editTextEmail);
        txtSenha = (EditText) findViewById(R.id.editTextSenha);
        txtConfirmaSenha = (EditText) findViewById(R.id.editTextConfirmaSenha);
        btnCadastro = (Button) findViewById(R.id.buttonCadastro);
        txvCadastrar = (TextView) findViewById(R.id.textViewTelaCadastro);

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtNome.getText().toString().isEmpty() && txtUsuario.getText().toString().isEmpty()
                        && txtEmail.getText().toString().isEmpty() && txtSenha.getText().toString().isEmpty()) {
                    Toast.makeText(CadastroActivity.this, "Por favor, informe todos os valores!", Toast.LENGTH_SHORT).show();
                    return;
                }

                postPessoa(txtNome.getText().toString(), txtUsuario.getText().toString(), txtEmail.getText().toString(), txtSenha.getText().toString());
            }
        });
    }
        private void postPessoa(String nome, String usuario, String email, String senha) {


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://localhost:8080/api/pessoa")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            PessoaService pessoaService = retrofit.create(PessoaService.class);

            Pessoa pessoa = new Pessoa(nome, usuario, email, senha);

            Call<Pessoa> call = pessoaService.createPost(pessoa);

            call.enqueue(new Callback<Pessoa>() {
                @Override
                public void onResponse(Call<Pessoa> call, Response<Pessoa> response) {
                    Toast.makeText(CadastroActivity.this, "Pessoa adicionada na api", Toast.LENGTH_SHORT).show();

                    txtNome.setText("");
                    txtEmail.setText("");
                    txtUsuario.setText("");
                    txtSenha.setText("");

                }

                @Override
                public void onFailure(Call<Pessoa> call, Throwable t) {
                    Toast.makeText(CadastroActivity.this, "Erro", Toast.LENGTH_SHORT).show();
                }
            });
        }

}