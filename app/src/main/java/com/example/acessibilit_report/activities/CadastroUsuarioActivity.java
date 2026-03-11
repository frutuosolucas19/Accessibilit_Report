package com.example.acessibilit_report.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.acessibilit_report.R;
import com.example.acessibilit_report.model.Pessoa;
import com.example.acessibilit_report.model.Usuario;
import com.example.acessibilit_report.retrofit.RetrofitInitializer;
import com.example.acessibilit_report.services.UsuarioService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private ImageView imgPerfil;
    private EditText txtNome;
    private EditText txtEmail;
    private EditText txtSenha;
    private EditText txtConfirmaSenha;
    private Button btnCadastro;
    private Button btnImagem;
    private TextView txvJaTenhoConta;

    private static final int REQ_PEGA_FOTO = 1;

    private Context context;
    private String imagemUriStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        context = this;

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        imgPerfil        = findViewById(R.id.imagemPerfil);
        txtNome          = findViewById(R.id.editTextNome);
        txtEmail         = findViewById(R.id.editTextEmail);
        txtSenha         = findViewById(R.id.editTextSenha);
        txtConfirmaSenha = findViewById(R.id.editTextConfirmaSenha);
        btnCadastro      = findViewById(R.id.buttonCadastro);
        btnImagem        = findViewById(R.id.buttonSelecionImagem);
        txvJaTenhoConta  = findViewById(R.id.textViewTelaCadastro);

        txvJaTenhoConta.setOnClickListener(v ->
                startActivity(new Intent(CadastroUsuarioActivity.this, LoginActivity.class)));

        btnImagem.setOnClickListener(v -> {
            Intent intentPegaFoto = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intentPegaFoto.setType("image/*");
            intentPegaFoto.addCategory(Intent.CATEGORY_OPENABLE);
            // pedir leitura (e escrita se precisar editar a imagem depois)
            intentPegaFoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intentPegaFoto.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intentPegaFoto.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intentPegaFoto, "Selecione uma imagem"), REQ_PEGA_FOTO);
        });

        btnCadastro.setOnClickListener(v -> {
            String nome     = txtNome.getText().toString().trim();
            String email    = txtEmail.getText().toString().trim().toLowerCase();
            String senha    = txtSenha.getText().toString();
            String confirma = txtConfirmaSenha.getText().toString();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirma.isEmpty()) {
                Toast.makeText(context, "Por favor, informe todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!senha.equals(confirma)) {
                Toast.makeText(context, "As senhas informadas não são iguais", Toast.LENGTH_SHORT).show();
                return;
            }

            btnCadastro.setEnabled(false);
            postUsuario(nome, email, imagemUriStr, senha, "normal");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode != REQ_PEGA_FOTO || data == null) return;

        Uri uri = data.getData();
        if (uri == null) return;

        try {
            getContentResolver().takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );
        } catch (SecurityException ignored) {
        }

        imgPerfil.setImageURI(uri);
        imagemUriStr = uri.toString();
    }

    private void postUsuario(String nome, String email, String imagemUri, String senha, String tipoUsuario) {
        Pessoa pessoa   = new Pessoa(nome, imagemUri);
        Usuario usuario = new Usuario(pessoa, email, senha, tipoUsuario);

        UsuarioService api = RetrofitInitializer.getUsuarioService(this);
        Call<Usuario> call = api.create(usuario);

        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                btnCadastro.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(CadastroUsuarioActivity.this, LoginActivity.class));
                        finish();
                    }, 1200);
                } else {
                    String msg = "Erro ao cadastrar";
                    if (response.code() == 409) msg = "E-mail já cadastrado";
                    else if (response.code() == 400) msg = "Dados inválidos";
                    else msg = "Falha (" + response.code() + ")";
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                btnCadastro.setEnabled(true);
                Toast.makeText(context, "Falha na requisição: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
