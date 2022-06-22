package com.example.acessibilit_report;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    private EditText txtNome;
    private EditText txtUsuario;
    private EditText txtEmail;
    private EditText txtSenha;
    private EditText txtConfirmaSenha;
    private Button btnEntrar;
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
        btnEntrar = (Button) findViewById(R.id.buttonLogin);
        txvCadastrar = (TextView) findViewById(R.id.textViewTelaCadastro);
    }
}